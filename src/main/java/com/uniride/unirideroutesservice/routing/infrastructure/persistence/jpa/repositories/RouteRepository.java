package com.uniride.unirideroutesservice.routing.infrastructure.persistence.jpa.repositories;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query(value = "SELECT r FROM Route r WHERE " +
            "(6371 * acos(cos(radians(:studentLat)) * cos(radians(r.startLocation.latitude)) * " +
            "cos(radians(r.startLocation.longitude) - radians(:studentLng)) + " +
            "sin(radians(:studentLat)) * sin(radians(r.startLocation.latitude)))) <= 0.5 " +
            "AND r.status = 'PENDING'")
    List<Route> findRoutesWithin500m(@Param("studentLat") Double studentLat, @Param("studentLng") Double studentLng);
}