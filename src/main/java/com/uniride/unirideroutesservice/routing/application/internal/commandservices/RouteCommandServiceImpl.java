package com.uniride.unirideroutesservice.routing.application.internal.commandservices;

import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenRouteServiceIntegration;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenStreetMapGeocodingService;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.PolylineDecoder;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.RemoveWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Visibility;
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

        // Agregamos al alumno temporalmente
        route.getWaypoints().add(new Location(command.lat(), command.lng(), command.address(), command.passengerId(), 0.0));

        // SOLUCIÓN AL ERROR DEL LAMBDA: Extraer variables como finales
        final double startLat = route.getStartLocation().getLatitude();
        final double startLng = route.getStartLocation().getLongitude();

        route.getWaypoints().sort((w1, w2) -> {
            double dist1 = polylineDecoder.calculatePointToPointDistance(startLat, startLng, w1.getLatitude(), w1.getLongitude());
            double dist2 = polylineDecoder.calculatePointToPointDistance(startLat, startLng, w2.getLatitude(), w2.getLongitude());
            return Double.compare(dist1, dist2);
        });

        updateRouteGeometry(route);
        route = routeRepository.save(route);

        for (Location wp : route.getWaypoints()) {
            Double realDistance = routeRepository.calculateDistanceAlongRoute(route.getId(), wp.getLatitude(), wp.getLongitude());
            wp.setDistanceFromStartKm(realDistance);
        }

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
        route = routeRepository.save(route);

        for (Location wp : route.getWaypoints()) {
            Double realDistance = routeRepository.calculateDistanceAlongRoute(route.getId(), wp.getLatitude(), wp.getLongitude());
            wp.setDistanceFromStartKm(realDistance);
        }

        return Optional.of(routeRepository.save(route));
    }

    private void updateRouteGeometry(Route route) {
        Map<String, Object> orsResult = orsIntegration.calculateOptimalRoute(route.getStartLocation(), route.getDestination(), route.getWaypoints());
        String polyline = (String) orsResult.get("geometry");

        route.setEncodedPolyline(polyline);
        route.setTotalDistanceKm((Double) orsResult.get("distanceKm"));
        route.setRoutePath(polylineDecoder.decodeToLineString(polyline));
    }

    @Override
    public void updateVisibility(Long routeId, Visibility visibility) {
        routeRepository.findById(routeId).ifPresent(route -> {
            route.setVisibility(visibility);
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