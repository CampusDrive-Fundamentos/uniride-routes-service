package com.uniride.unirideroutesservice.routing.interfaces.rest.resources;

public record CampusResource(
        String name,          // Ejemplo: "UPC_MONTERRICO"
        String description,   // Ejemplo: "UPC Sede Monterrico"
        Double latitude,
        Double longitude
) {}