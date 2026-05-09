package com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByStartCampusAndStatus(UniversityCampus startCampus, RouteStatus status);

    Optional<Route> findFirstByLeaderIdAndStatusNot(Long leaderId, RouteStatus status);

    @Query("SELECT r FROM Route r JOIN r.waypoints w WHERE w.passengerId = :passengerId AND r.status <> 'COMPLETED'")
    Optional<Route> findActiveRouteByPassengerId(@Param("passengerId") Long passengerId);

    // Búsqueda de rutas a 500 metros
    @Query(value = "SELECT * FROM routes r WHERE r.start_campus = :campus AND r.status = 'PENDING' " +
            "AND ST_DWithin(r.route_path::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, 500)",
            nativeQuery = true)
    List<Route> findNearbyRoutes(@Param("campus") String campus, @Param("lat") Double lat, @Param("lng") Double lng);

    // NUEVO: Calcula la distancia EXACTA de la calle desde la Universidad hasta la parada del alumno
    @Query(value = "SELECT ST_Length(ST_LineSubstring(r.route_path::geometry, 0, " +
            "ST_LineLocatePoint(r.route_path::geometry, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geometry))::geography) / 1000.0 " +
            "FROM routes r WHERE r.id = :routeId", nativeQuery = true)
    Double calculateDistanceAlongRoute(@Param("routeId") Long routeId, @Param("lat") Double lat, @Param("lng") Double lng);
}