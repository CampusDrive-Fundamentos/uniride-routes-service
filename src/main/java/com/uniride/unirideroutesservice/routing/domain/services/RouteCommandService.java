package com.uniride.unirideroutesservice.routing.domain.services;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.RemoveWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;

import java.util.Optional;

public interface RouteCommandService {
    Optional<Route> handle(CreateRouteCommand command);
    Optional<Route> handle(AddWaypointCommand command);

    // NUEVOS MÉTODOS
    void updateStatus(Long routeId, RouteStatus status);
    void deleteRoute(Long routeId);
    Optional<Route> handle(RemoveWaypointCommand command);
}