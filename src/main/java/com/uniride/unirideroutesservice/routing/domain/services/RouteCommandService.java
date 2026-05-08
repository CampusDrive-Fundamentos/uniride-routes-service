package com.uniride.unirideroutesservice.routing.domain.services;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;

import java.util.Optional;

public interface RouteCommandService {
    // Para crear la ruta inicial
    Optional<Route> handle(CreateRouteCommand command);

    // NUEVO: Para añadir las paradas de los seguidores
    Optional<Route> handle(AddWaypointCommand command);
}