package com.uniride.unirideroutesservice.routing.domain.model.queries;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;

public record GetAllSearchableRoutesByCampusQuery(UniversityCampus campus) {
}