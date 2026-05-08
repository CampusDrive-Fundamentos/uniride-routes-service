package com.uniride.unirideroutesservice.routing.interfaces.rest.resources;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
public record CreateRouteResource(Long leaderId, UniversityCampus campus, String destinationAddress) {}