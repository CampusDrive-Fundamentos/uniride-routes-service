package com.uniride.unirideroutesservice.routing.application.internal.queryservices;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetAllSearchableRoutesByCampusQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Visibility;
import com.uniride.unirideroutesservice.routing.domain.services.RouteQueryService;
import com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteQueryServiceImpl implements RouteQueryService {
    private final RouteRepository routeRepository;

    public RouteQueryServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Optional<Route> handle(GetRouteByIdQuery query) {
        return routeRepository.findById(query.routeId());
    }

    @Override
    public List<Route> handle(SearchNearbyRoutesQuery query) {
        return routeRepository.findNearbyRoutes(query.campus().name(), query.studentLat(), query.studentLng());
    }

    @Override
    public List<Route> handle(GetAllSearchableRoutesByCampusQuery query) {
        return routeRepository.findByStartCampusAndVisibility(query.campus(), Visibility.SEARCHABLE);
    }
}