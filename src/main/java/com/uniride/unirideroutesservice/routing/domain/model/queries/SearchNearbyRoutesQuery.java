package com.uniride.unirideroutesservice.routing.domain.model.queries;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;

public record SearchNearbyRoutesQuery(UniversityCampus campus, Double studentLat, Double studentLng) {}