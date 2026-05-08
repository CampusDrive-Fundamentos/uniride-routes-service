package com.uniride.unirideroutesservice.routing.interfaces.rest;

import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/routes")
public class RoutesController {

    private final RouteCommandService routeCommandService;
    private final RouteQueryService routeQueryService; // (Asegúrate de actualizar este servicio con la nueva firma del Repo)

    public RoutesController(RouteCommandService routeCommandService, RouteQueryService routeQueryService) {
        this.routeCommandService = routeCommandService;
        this.routeQueryService = routeQueryService;
    }

    @PostMapping
    @Operation(summary = "PANTALLA 3B: Crear Ruta (Columna Vertebral)")
    public ResponseEntity<?> createRoute(@RequestBody CreateRouteRequest request) {
        // Obtenemos el ID del estudiante directamente del Token de IAM
        String leaderIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long leaderId = Long.parseLong(leaderIdStr);

        var command = new CreateRouteCommand(leaderId, request.campus(), request.address(), request.lat(), request.lng());
        return ResponseEntity.ok(routeCommandService.handle(command).orElseThrow());
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

    @PutMapping("/{routeId}/waypoints")
    @Operation(summary = "PANTALLA 3A: Añadir Parada (Seguidor se une)")
    public ResponseEntity<?> addWaypoint(@PathVariable Long routeId, @RequestBody WaypointRequest request) {
        var command = new AddWaypointCommand(routeId, request.lat(), request.lng(), request.address());
        return ResponseEntity.ok(routeCommandService.handle(command).orElseThrow());
    }
}

// Records para los Requests (Puedes colocarlos en sus propios archivos en el paquete /resources)
record CreateRouteRequest(UniversityCampus campus, String address, Double lat, Double lng) {}
record WaypointRequest(Double lat, Double lng, String address) {}