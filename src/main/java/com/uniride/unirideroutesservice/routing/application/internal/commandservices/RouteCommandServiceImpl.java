package com.uniride.unirideroutesservice.routing.application.internal.commandservices;

import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenRouteServiceIntegration;
import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenStreetMapGeocodingService;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
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

    public RouteCommandServiceImpl(RouteRepository routeRepository, OpenStreetMapGeocodingService geocodingService,
                                   OpenRouteServiceIntegration orsIntegration) {
        this.routeRepository = routeRepository;
        this.geocodingService = geocodingService;
        this.orsIntegration = orsIntegration;
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
        route.getWaypoints().add(new Location(command.lat(), command.lng(), command.address()));
        updateRouteGeometry(route);
        return Optional.of(routeRepository.save(route));
    }

    private void updateRouteGeometry(Route route) {
        // Pedimos el Polyline optimizado a OpenRouteService
        Map<String, Object> orsResult = orsIntegration.calculateOptimalRoute(route.getStartLocation(), route.getDestination(), route.getWaypoints());
        String polyline = (String) orsResult.get("geometry");
        Double distance = (Double) orsResult.get("distanceKm");

        // Guardamos texto puro y la distancia
        route.setEncodedPolyline(polyline);
        route.setTotalDistanceKm(distance);
    }
}