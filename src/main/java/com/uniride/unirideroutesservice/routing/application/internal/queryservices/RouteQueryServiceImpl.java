package com.uniride.unirideroutesservice.routing.application.internal.queryservices;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetAllPendingRoutesByCampusQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
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
        // PostGIS hace el cálculo de 500 metros en milisegundos directamente en el motor SQL
        return routeRepository.findNearbyRoutes(query.campus().name(), query.studentLat(), query.studentLng());
    }

    @Override
    public Optional<Route> findActiveRouteByLeaderId(Long leaderId) {
        return routeRepository.findFirstByLeaderIdAndStatusNot(leaderId, RouteStatus.COMPLETED);
    }

    @Override
    public List<Route> handle(GetAllPendingRoutesByCampusQuery query) {
        return routeRepository.findByStartCampusAndStatus(query.campus(), RouteStatus.PENDING);
    }

    @Override
    public Optional<Route> findActiveRouteByPassengerId(Long passengerId) {
        return routeRepository.findActiveRouteByPassengerId(passengerId);
    }
}