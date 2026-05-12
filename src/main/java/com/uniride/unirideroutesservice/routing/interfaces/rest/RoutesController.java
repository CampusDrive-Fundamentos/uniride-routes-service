package com.uniride.unirideroutesservice.routing.interfaces.rest;

import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.RemoveWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetAllSearchableRoutesByCampusQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Visibility;
import com.uniride.unirideroutesservice.routing.domain.services.RouteCommandService;
import com.uniride.unirideroutesservice.routing.domain.services.RouteQueryService;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.CreateRouteResource;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.RouteResource;
import com.uniride.unirideroutesservice.routing.interfaces.rest.transform.RouteResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Routes", description = "Route Management Endpoints (GIS Domain)")
public class RoutesController {

    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService;

    public RoutesController(RouteCommandService routeCommandService, RouteQueryService routeQueryService) {
        this.routeCommandService = routeCommandService;
        this.routeQueryService = routeQueryService;
    }

    // Método auxiliar para extraer el ID del usuario del token JWT para la creación
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            return Long.parseLong(userId.toString());
        }
        return 1L; // Fallback temporal para pruebas locales
    }

    @PostMapping
    @Operation(summary = "Create a new route (Blueprint)")
    public ResponseEntity<RouteResource> createRoute(@RequestBody CreateRouteResource resource, HttpServletRequest request) {
        Long leaderId = getUserIdFromRequest(request);
        CreateRouteCommand command = new CreateRouteCommand(
                leaderId,
                UniversityCampus.valueOf(resource.campus()),
                resource.destinationAddress(),
                resource.destinationLat(),
                resource.destinationLng()
        );
        return routeCommandService.handle(command)
                .map(route -> new ResponseEntity<>(RouteResourceFromEntityAssembler.toResourceFromEntity(route), HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{routeId}")
    @Operation(summary = "Get route details by ID")
    public ResponseEntity<RouteResource> getRouteById(@PathVariable Long routeId) {
        return routeQueryService.handle(new GetRouteByIdQuery(routeId))
                .map(route -> ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search nearby SEARCHABLE routes within 500m using PostGIS")
    public ResponseEntity<List<RouteResource>> searchNearbyRoutes(
            @RequestParam String campus,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        List<RouteResource> routes = routeQueryService.handle(new SearchNearbyRoutesQuery(UniversityCampus.valueOf(campus), lat, lng))
                .stream()
                .map(RouteResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/campus/{campus}")
    @Operation(summary = "Get all SEARCHABLE routes by campus")
    public ResponseEntity<List<RouteResource>> getAllSearchableRoutesByCampus(@PathVariable String campus) {
        List<RouteResource> routes = routeQueryService.handle(new GetAllSearchableRoutesByCampusQuery(UniversityCampus.valueOf(campus)))
                .stream()
                .map(RouteResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/{routeId}/waypoints")
    @Operation(summary = "Add a waypoint to the route, auto-sort passengers, and recalculate precise distances")
    public ResponseEntity<RouteResource> addWaypoint(@PathVariable Long routeId, @RequestBody WaypointRequest request, HttpServletRequest httpRequest) {
        Long passengerId = getUserIdFromRequest(httpRequest);
        AddWaypointCommand command = new AddWaypointCommand(routeId, request.lat(), request.lng(), request.address(), passengerId);
        return routeCommandService.handle(command)
                .map(route -> ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{routeId}/waypoints")
    @Operation(summary = "Remove a waypoint from the route and recalculate distances")
    public ResponseEntity<RouteResource> removeWaypoint(@PathVariable Long routeId, @RequestParam Double lat, @RequestParam Double lng) {
        RemoveWaypointCommand command = new RemoveWaypointCommand(routeId, lat, lng);
        return routeCommandService.handle(command)
                .map(route -> ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{routeId}/visibility")
    @Operation(summary = "Update route visibility (SEARCHABLE / HIDDEN) - To be called by Booking Service")
    public ResponseEntity<Void> updateVisibility(@PathVariable Long routeId, @RequestParam String visibility) {
        // Este endpoint es el que usará tu futuro microservicio de Booking para esconder la ruta
        routeCommandService.updateVisibility(routeId, Visibility.valueOf(visibility.toUpperCase()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{routeId}")
    @Operation(summary = "Delete route completely")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId) {
        routeCommandService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }
}