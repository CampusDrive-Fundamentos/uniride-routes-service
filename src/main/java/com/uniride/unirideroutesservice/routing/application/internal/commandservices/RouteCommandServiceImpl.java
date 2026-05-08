package com.uniride.unirideroutesservice.routing.application.internal.commandservices;

import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenRouteServiceIntegration;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenStreetMapGeocodingService;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.PolylineDecoder;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.RemoveWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
import com.uniride.unirideroutesservice.routing.domain.services.RouteCommandService;
import com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class RouteCommandServiceImpl implements RouteCommandService {
    private final RouteRepository routeRepository;
    private final OpenStreetMapGeocodingService geocodingService;
    private final OpenRouteServiceIntegration orsIntegration;
    private final PolylineDecoder polylineDecoder;

    public RouteCommandServiceImpl(RouteRepository routeRepository, OpenStreetMapGeocodingService geocodingService,
                                   OpenRouteServiceIntegration orsIntegration, PolylineDecoder polylineDecoder) {
        this.routeRepository = routeRepository;
        this.geocodingService = geocodingService;
        this.orsIntegration = orsIntegration;
        this.polylineDecoder = polylineDecoder;
    }

    @Override
    public Optional<Route> handle(CreateRouteCommand command) {
        Location destinationLocation;
        if (command.destinationLat() != null && command.destinationLng() != null) {
            destinationLocation = new Location(command.destinationLat(), command.destinationLng(), command.destinationAddress());
        } else {
            destinationLocation = geocodingService.geocodeAddress(command.destinationAddress());
        }
        Route route = new Route(command, destinationLocation);
        updateRouteGeometry(route);
        return Optional.of(routeRepository.save(route));
    }

    @Override
    public Optional<Route> handle(AddWaypointCommand command) {
        Optional<Route> routeOpt = routeRepository.findById(command.routeId());
        if (routeOpt.isEmpty()) return Optional.empty();

        Route route = routeOpt.get();
        Double distance = polylineDecoder.calculateHaversineDistance(
                route.getStartLocation().getLatitude(),
                route.getStartLocation().getLongitude(),
                command.lat(),
                command.lng()
        );

        route.getWaypoints().add(new Location(command.lat(), command.lng(), command.address(), command.passengerId(), distance));
        updateRouteGeometry(route);
        return Optional.of(routeRepository.save(route));
    }

    @Override
    public Optional<Route> handle(RemoveWaypointCommand command) {
        Optional<Route> routeOpt = routeRepository.findById(command.routeId());
        if (routeOpt.isEmpty()) return Optional.empty();

        Route route = routeOpt.get();
        route.getWaypoints().removeIf(w ->
                Math.abs(w.getLatitude() - command.lat()) < 0.0001 &&
                        Math.abs(w.getLongitude() - command.lng()) < 0.0001
        );
        updateRouteGeometry(route);
        return Optional.of(routeRepository.save(route));
    }

    private void updateRouteGeometry(Route route) {
        Map<String, Object> orsResult = orsIntegration.calculateOptimalRoute(route.getStartLocation(), route.getDestination(), route.getWaypoints());
        route.setEncodedPolyline((String) orsResult.get("geometry"));
        route.setTotalDistanceKm((Double) orsResult.get("distanceKm"));
    }

    @Override
    public void updateStatus(Long routeId, RouteStatus status) {
        routeRepository.findById(routeId).ifPresent(route -> {
            route.setStatus(status);
            routeRepository.save(route);
        });
    }

    @Override
    public void deleteRoute(Long routeId) {
        if (routeRepository.existsById(routeId)) {
            routeRepository.deleteById(routeId);
        }
    }
}