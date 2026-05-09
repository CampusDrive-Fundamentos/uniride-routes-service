package com.uniride.unirideroutesservice.routing.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // <-- AÑADIDO PARA EVITAR EL ERROR

@Embeddable
@Getter
@Setter // <-- AÑADIDO PARA EVITAR EL ERROR
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double latitude;
    private Double longitude;
    private String address;
    private Long passengerId;

    @Column(name = "distance_from_start_km")
    private Double distanceFromStartKm;

    public Location(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.passengerId = null;
        this.distanceFromStartKm = 0.0;
    }
}