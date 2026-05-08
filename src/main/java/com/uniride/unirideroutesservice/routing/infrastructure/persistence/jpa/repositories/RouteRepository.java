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

    // Solo traemos las rutas que salen de la misma universidad y están esperando pasajeros.
    // La magia de los 500m la haremos en Java.
    List<Route> findByStartCampusAndStatus(UniversityCampus startCampus, RouteStatus status);

    Optional<Route> findFirstByLeaderIdAndStatusNot(Long leaderId, RouteStatus status);
    // Buscar ruta activa donde el estudiante sea un SEGUIDOR (esté en los waypoints)
    @Query("SELECT r FROM Route r JOIN r.waypoints w WHERE w.passengerId = :passengerId AND r.status <> 'COMPLETED'")
    Optional<Route> findActiveRouteByPassengerId(@Param("passengerId") Long passengerId);
}