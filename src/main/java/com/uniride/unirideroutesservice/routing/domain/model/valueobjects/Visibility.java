package com.uniride.unirideroutesservice.routing.domain.model.valueobjects;

public enum Visibility {
    SEARCHABLE, // Visible en el radar de 500m
    HIDDEN      // Oculto (Booking nos ordenará ocultarlo cuando el auto se llene)
}