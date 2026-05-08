package com.uniride.unirideroutesservice.routing.application.internal.commandservices;

import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.OpenStreetMapGeocodingService;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import com.uniride.unirideroutesservice.routing.domain.services.RouteCommandService;
import com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories.RouteRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RouteCommandServiceImpl implements RouteCommandService {
    private final RouteRepository routeRepository;
    private final OpenStreetMapGeocodingService geocodingService;

    public RouteCommandServiceImpl(RouteRepository routeRepository, OpenStreetMapGeocodingService geocodingService) {
        this.routeRepository = routeRepository;
        this.geocodingService = geocodingService;
    }

    @Override
    public Optional<Route> handle(CreateRouteCommand command) {
        Location destinationLocation = geocodingService.geocodeAddress(command.destinationAddress());
        Route route = new Route(command, destinationLocation);
        return Optional.of(routeRepository.save(route));
    }
}