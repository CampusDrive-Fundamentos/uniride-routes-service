package com.uniride.unirideroutesservice.routing.application.internal.queryservices;

import com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap.PolylineDecoder;
import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location; // <-- ¡ESTE ES EL IMPORT QUE FALTABA!
import com.uniride.unirideroutesservice.routing.domain.services.RouteQueryService;
import com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteQueryServiceImpl implements RouteQueryService {
    private final RouteRepository routeRepository;
    private final PolylineDecoder polylineDecoder;

    public RouteQueryServiceImpl(RouteRepository routeRepository, PolylineDecoder polylineDecoder) {
        this.routeRepository = routeRepository;
        this.polylineDecoder = polylineDecoder;
    }

    @Override
    public Optional<Route> handle(GetRouteByIdQuery query) {
        return routeRepository.findById(query.routeId());
    }

    @Override
    public List<Route> handle(SearchNearbyRoutesQuery query) {
        // 1. Obtenemos TODAS las rutas pendientes que salgan de la MISMA universidad (Origen Estricto)
        List<Route> allCampusRoutes = routeRepository.findByStartCampusAndStatus(query.campus(), RouteStatus.PENDING);

        List<Route> nearbyRoutes = new ArrayList<>();

        // 2. Filtramos en Java usando el tubo de 500m
        for (Route route : allCampusRoutes) {
            if (route.getEncodedPolyline() != null && !route.getEncodedPolyline().isEmpty()) {
                // Convertimos el texto de la calle a puntitos GPS
                List<Location> routePoints = polylineDecoder.decodeToList(route.getEncodedPolyline());

                // Vemos si el Seguidor está cerca
                // Java se encarga de convertir los 'Double' (objetos) a 'double' (primitivos) automáticamente
                boolean isNear = polylineDecoder.isPointNearLine(routePoints, query.studentLat(), query.studentLng());

                if (isNear) {
                    nearbyRoutes.add(route);
                }
            }
        }
        return nearbyRoutes;
    }
}