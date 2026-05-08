package com.uniride.unirideroutesservice.routing.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double latitude;
    private Double longitude;
    private String address;
    private Long passengerId;

    @Column(name = "distance_from_start_km")
    private Double distanceFromStartKm;

    // Constructor para Origen (Campus) y Destino final del Líder
    public Location(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.passengerId = null;
        this.distanceFromStartKm = 0.0;
    }
}