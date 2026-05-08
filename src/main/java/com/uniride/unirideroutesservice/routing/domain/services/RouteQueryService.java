package com.uniride.unirideroutesservice.routing.domain.services;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;

import java.util.List;
import java.util.Optional;

public interface RouteQueryService {
    Optional<Route> handle(GetRouteByIdQuery query);
    List<Route> handle(SearchNearbyRoutesQuery query); // <-- NUEVO MÉTODO
}