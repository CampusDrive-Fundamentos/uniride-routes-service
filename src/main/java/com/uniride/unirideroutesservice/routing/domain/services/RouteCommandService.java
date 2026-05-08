package com.uniride.unirideroutesservice.routing.domain.services;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import java.util.Optional;

public interface RouteCommandService {
    Optional<Route> handle(CreateRouteCommand command);
}