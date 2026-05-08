package com.uniride.unirideroutesservice.routing.domain.model.commands;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
public record CreateRouteCommand(Long leaderId, UniversityCampus campus, String destinationAddress) {}