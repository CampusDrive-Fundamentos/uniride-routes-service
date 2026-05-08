package com.uniride.unirideroutesservice.routing.interfaces.rest;

import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.UniversityCampus;
import com.uniride.unirideroutesservice.routing.domain.services.RouteCommandService;
import com.uniride.unirideroutesservice.routing.domain.services.RouteQueryService;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.RouteResource;
import com.uniride.unirideroutesservice.routing.interfaces.rest.transform.RouteResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.uniride.unirideroutesservice.routing.interfaces.rest.resources.CampusResource;
import java.util.Arrays;
import java.util.stream.Collectors;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600) // PERMITE QUE FLUTTER WEB SE CONECTE SIN ERRORES CORS
@RestController
@RequestMapping(value = "/api/v1/routes")
public class RoutesController {

    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService;

    public RoutesController(RouteCommandService routeCommandService, RouteQueryService routeQueryService) {
        this.routeCommandService = routeCommandService;
        this.routeQueryService = routeQueryService;
    }

    @PostMapping
    @Operation(summary = "PANTALLA 3B: Crear Ruta (Columna Vertebral)")
    public ResponseEntity<RouteResource> createRoute(@RequestBody CreateRouteRequest request) {
        String leaderIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long leaderId = Long.parseLong(leaderIdStr);

        var command = new CreateRouteCommand(leaderId, request.campus(), request.address(), request.lat(), request.lng());
        var route = routeCommandService.handle(command).orElseThrow();

        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }

    @GetMapping("/search")
    @Operation(summary = "PANTALLA 2: Buscar rutas a 500m del tubo geográfico")
    public ResponseEntity<List<RouteResource>> searchNearbyRoutes(@RequestParam UniversityCampus campus, @RequestParam Double lat, @RequestParam Double lng) {
        var query = new SearchNearbyRoutesQuery(campus, lat, lng);
        var routes = routeQueryService.handle(query);

        var routeResources = routes.stream()
                .map(RouteResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(routeResources);
    }

    @GetMapping("/{routeId}")
    @Operation(summary = "Obtener Ruta Específica (Para refrescar pantalla en Flutter)")
    public ResponseEntity<RouteResource> getRouteById(@PathVariable Long routeId) {
        var query = new GetRouteByIdQuery(routeId);
        var route = routeQueryService.handle(query).orElseThrow();
        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }

    @PutMapping("/{routeId}/waypoints")
    @Operation(summary = "PANTALLA 3A: Añadir Parada (Seguidor se une)")
    public ResponseEntity<RouteResource> addWaypoint(@PathVariable Long routeId, @RequestBody WaypointRequest request) {
        var command = new AddWaypointCommand(routeId, request.lat(), request.lng(), request.address());
        var route = routeCommandService.handle(command).orElseThrow();

        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }

    @GetMapping("/campuses")
    @Operation(summary = "Obtener lista de sedes universitarias disponibles",
            description = "Devuelve todas las sedes con sus coordenadas para que Flutter las muestre en el menú.")
    public ResponseEntity<List<CampusResource>> getCampuses() {
        var campuses = Arrays.stream(UniversityCampus.values())
                .map(campus -> new CampusResource(
                        campus.name(),
                        campus.getDescription(),
                        campus.getLatitude(),
                        campus.getLongitude()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(campuses);
    }
}

// Records para recibir los JSON de Flutter
record CreateRouteRequest(UniversityCampus campus, String address, Double lat, Double lng) {}
record WaypointRequest(Double lat, Double lng, String address) {}