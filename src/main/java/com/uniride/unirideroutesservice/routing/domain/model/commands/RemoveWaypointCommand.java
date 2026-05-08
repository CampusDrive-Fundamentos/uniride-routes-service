package com.uniride.unirideroutesservice.routing.domain.model.commands;

public record RemoveWaypointCommand(Long routeId, Double lat, Double lng) {}