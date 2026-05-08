package com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    // Solo traemos las rutas que salen de la misma universidad y están esperando pasajeros.
    // La magia de los 500m la haremos en Java.
    List<Route> findByStartCampusAndStatus(UniversityCampus startCampus, RouteStatus status);
}