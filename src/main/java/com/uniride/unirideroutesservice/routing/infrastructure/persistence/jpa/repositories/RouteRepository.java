package com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Visibility;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByStartCampusAndVisibility(UniversityCampus startCampus, Visibility visibility);

    // Búsqueda de rutas a 500 metros (El motor de Matchmaking PostGIS)
    @Query(value = "SELECT * FROM routes r WHERE r.start_campus = :campus AND r.visibility = 'SEARCHABLE' " +
            "AND ST_DWithin(r.route_path::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, 500)",
            nativeQuery = true)
    List<Route> findNearbyRoutes(@Param("campus") String campus, @Param("lat") Double lat, @Param("lng") Double lng);

    // Calcula la distancia EXACTA de la calle
    @Query(value = "SELECT ST_Length(ST_LineSubstring(r.route_path::geometry, 0, " +
            "ST_LineLocatePoint(r.route_path::geometry, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geometry))::geography) / 1000.0 " +
            "FROM routes r WHERE r.id = :routeId", nativeQuery = true)
    Double calculateDistanceAlongRoute(@Param("routeId") Long routeId, @Param("lat") Double lat, @Param("lng") Double lng);
}