// src/main/java/com/uniride/unirideroutesservice/routing/interfaces/rest/resources/RouteResource.java
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
        String encodedPolyline,
        Double totalDistanceKm,
        List<Location> waypoints // <-- Contendrá la Location con passengerId y distanceFromStartKm
) {}