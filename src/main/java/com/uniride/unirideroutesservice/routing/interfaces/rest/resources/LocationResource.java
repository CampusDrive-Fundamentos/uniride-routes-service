package com.uniride.unirideroutesservice.routing.interfaces.rest.resources;

public record LocationResource(
        Double latitude,
        Double longitude,
        String address,
        Long passengerId,
        Double distanceFromStartKm
) {
}