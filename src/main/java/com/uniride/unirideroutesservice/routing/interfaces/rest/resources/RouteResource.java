package com.uniride.unirideroutesservice.routing.interfaces.rest.resources;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import java.util.List;

public record RouteResource(
        Long id,
        Long leaderId,
        String startCampus,
        Double startLat,
        Double startLng,
        String destinationAddress,
        Double destLat,
        Double destLng,
        String status,
        String encodedPolyline,     // ¡CRÍTICO PARA FLUTTER!
        Double totalDistanceKm,     // ¡CRÍTICO PARA FLUTTER!
        List<Location> waypoints    // ¡CRÍTICO PARA FLUTTER!
) {}