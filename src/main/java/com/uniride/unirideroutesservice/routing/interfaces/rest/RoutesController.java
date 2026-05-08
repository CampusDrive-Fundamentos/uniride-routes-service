package com.uniride.unirideroutesservice.routing.interfaces.rest;

import com.uniride.unirideroutesservice.routing.domain.model.aggregates.Route;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRoutesWithin500mQuery;
import com.uniride.unirideroutesservice.routing.domain.services.RouteCommandService;
import com.uniride.unirideroutesservice.routing.domain.services.RouteQueryService;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.CreateRouteResource;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.RouteResource;
import com.uniride.unirideroutesservice.routing.interfaces.rest.transform.CreateRouteCommandFromResourceAssembler;
import com.uniride.unirideroutesservice.routing.interfaces.rest.transform.RouteResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/routes")
@Tag(name = "Routes", description = "Endpoints para la gestión de Rutas de CampusDrive")
public class RoutesController {

    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService;

    public RoutesController(RouteCommandService routeCommandService, RouteQueryService routeQueryService) {
        this.routeCommandService = routeCommandService;
        this.routeQueryService = routeQueryService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva Ruta", description = "Crea una ruta desde una Sede Universitaria hacia una dirección destino usando OpenStreetMap.")
    public ResponseEntity<RouteResource> createRoute(@RequestBody CreateRouteResource resource) {
        var command = CreateRouteCommandFromResourceAssembler.toCommandFromResource(resource);
        Optional<Route> route = routeCommandService.handle(command);

        if (route.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var routeResource = RouteResourceFromEntityAssembler.toResourceFromEntity(route.get());
        return new ResponseEntity<>(routeResource, HttpStatus.CREATED);
    }

    @GetMapping("/{routeId}")
    @Operation(summary = "Obtener Ruta por ID", description = "Devuelve los detalles de una ruta específica.")
    public ResponseEntity<RouteResource> getRouteById(@PathVariable Long routeId) {
        var getRouteByIdQuery = new GetRouteByIdQuery(routeId);
        var route = routeQueryService.handle(getRouteByIdQuery);

        if (route.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var routeResource = RouteResourceFromEntityAssembler.toResourceFromEntity(route.get());
        return ResponseEntity.ok(routeResource);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Obtener rutas a menos de 500m", description = "Aplica el algoritmo de 500m usando la latitud y longitud del Seguidor para encontrar rutas pendientes.")
    public ResponseEntity<List<RouteResource>> getNearbyRoutes(@RequestParam Double studentLat, @RequestParam Double studentLng) {
        var getRoutesWithin500mQuery = new GetRoutesWithin500mQuery(studentLat, studentLng);
        var routes = routeQueryService.handle(getRoutesWithin500mQuery);

        var routeResources = routes.stream()
                .map(RouteResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(routeResources);
    }
}