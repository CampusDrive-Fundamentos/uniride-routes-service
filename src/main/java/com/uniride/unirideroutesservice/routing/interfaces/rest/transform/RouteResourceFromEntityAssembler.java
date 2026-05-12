package com.uniride.unirideroutesservice.routing.interfaces.rest.transform;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.LocationResource;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.RouteResource;

import java.util.stream.Collectors;

public class RouteResourceFromEntityAssembler {
    public static RouteResource toResourceFromEntity(Route entity) {
        return new RouteResource(
                entity.getId(),
                entity.getLeaderId(),
                entity.getStartCampus().name(),
                new LocationResource(entity.getStartLocation().getLatitude(), entity.getStartLocation().getLongitude(), entity.getStartLocation().getAddress(), null, 0.0),
                new LocationResource(entity.getDestination().getLatitude(), entity.getDestination().getLongitude(), entity.getDestination().getAddress(), null, 0.0),
                entity.getEncodedPolyline(),
                entity.getTotalDistanceKm(),
                entity.getVisibility().name(), // Ahora lee el Enum Visibility
                entity.getWaypoints().stream()
                        .map(wp -> new LocationResource(wp.getLatitude(), wp.getLongitude(), wp.getAddress(), wp.getPassengerId(), wp.getDistanceFromStartKm()))
                        .collect(Collectors.toList())
        );
    }
}