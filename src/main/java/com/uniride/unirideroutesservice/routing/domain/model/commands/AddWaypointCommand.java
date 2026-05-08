package com.uniride.unirideroutesservice.routing.domain.model.commands;

public record AddWaypointCommand(
        Long routeId,
        Double lat,
        Double lng,
        String address,
        Long passengerId // <-- Ya agregamos el campo faltante
) {}