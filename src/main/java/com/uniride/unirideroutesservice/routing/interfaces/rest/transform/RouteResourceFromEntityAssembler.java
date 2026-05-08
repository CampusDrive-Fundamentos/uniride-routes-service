package com.uniride.unirideroutesservice.routing.interfaces.rest.transform;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.RouteResource;

public class RouteResourceFromEntityAssembler {
    public static RouteResource toResourceFromEntity(Route entity) {
        return new RouteResource(
                entity.getId(),
                entity.getLeaderId(),
                entity.getStartCampus().name(),
                entity.getStartLocation().getLatitude(),
                entity.getStartLocation().getLongitude(),
                entity.getDestination().getAddress(),
                entity.getDestination().getLatitude(),
                entity.getDestination().getLongitude(),
                entity.getStatus().name(),
                entity.getEncodedPolyline(), // Empaquetamos la línea
                entity.getTotalDistanceKm(), // Empaquetamos la distancia
                entity.getWaypoints()        // Empaquetamos las paradas
        );
    }
}