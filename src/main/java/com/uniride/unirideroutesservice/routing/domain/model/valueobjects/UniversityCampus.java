package com.uniride.unirideroutesservice.routing.domain.model.valueobjects;
import lombok.Getter;

@Getter
public enum UniversityCampus {
    UPC_MONTERRICO(-12.104061, -76.962902, "UPC Sede Monterrico"),
    UPC_SAN_ISIDRO(-12.087450, -77.049960, "UPC Sede San Isidro"),
    UPC_SAN_MIGUEL(-12.076839, -77.093315, "UPC Sede San Miguel"),
    UPC_VILLA(-12.196942, -76.999655, "UPC Sede Villa"),
    UNMSM(-12.055938, -77.081735, "Universidad Nacional Mayor de San Marcos");

    private final double latitude;
    private final double longitude;
    private final String description;

    UniversityCampus(double latitude, double longitude, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }
}