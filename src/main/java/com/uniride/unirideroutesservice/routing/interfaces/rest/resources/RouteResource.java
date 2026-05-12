package com.uniride.unirideroutesservice.routing.interfaces.rest.resources;

import java.util.List;

public record RouteResource(
        Long id,
        Long leaderId,
        String startCampus,
        LocationResource startLocation,
        LocationResource destination,
        String encodedPolyline,
        Double totalDistanceKm,
        String visibility, // Se cambió de status a visibility
        List<LocationResource> waypoints
) {
}