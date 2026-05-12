package com.uniride.unirideroutesservice.routing.interfaces.rest;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for adding a new passenger's stop (waypoint) to the route")
public record WaypointRequest(
        @Schema(description = "Latitude of the student's location", example = "-12.1055")
        Double lat,

        @Schema(description = "Longitude of the student's location", example = "-76.9722")
        Double lng,

        @Schema(description = "Text address of the location", example = "Av. Primavera 123, Surco")
        String address
) {
}