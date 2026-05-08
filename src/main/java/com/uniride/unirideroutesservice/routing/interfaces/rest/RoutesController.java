package com.uniride.unirideroutesservice.routing.interfaces.rest;

import com.uniride.unirideroutesservice.routing.domain.model.commands.AddWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.CreateRouteCommand;
import com.uniride.unirideroutesservice.routing.domain.model.commands.RemoveWaypointCommand;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetAllPendingRoutesByCampusQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.GetRouteByIdQuery;
import com.uniride.unirideroutesservice.routing.domain.model.queries.SearchNearbyRoutesQuery;
import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.RouteStatus;
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

@CrossOrigin(origins = "*", maxAge = 3600)
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
    @Operation(summary = "Crear una nueva ruta principal",
            description = "Genera la ruta inicial del estudiante líder desde una sede universitaria hacia su destino, trazando el camino óptimo.")
    public ResponseEntity<RouteResource> createRoute(@RequestBody CreateRouteRequest request) {
        String leaderIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long leaderId = Long.parseLong(leaderIdStr);

        var command = new CreateRouteCommand(leaderId, request.campus(), request.address(), request.lat(), request.lng());
        var route = routeCommandService.handle(command).orElseThrow();

        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar rutas cercanas",
            description = "Obtiene las rutas pendientes cuya trayectoria óptima se encuentre a un radio de 500 metros del destino del estudiante seguidor.")
    public ResponseEntity<List<RouteResource>> searchNearbyRoutes(@RequestParam UniversityCampus campus, @RequestParam Double lat, @RequestParam Double lng) {
        var query = new SearchNearbyRoutesQuery(campus, lat, lng);
        var routes = routeQueryService.handle(query);

        var routeResources = routes.stream()
                .map(RouteResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(routeResources);
    }

    @GetMapping("/{routeId}")
    @Operation(summary = "Obtener detalles de una ruta",
            description = "Devuelve la información completa de una ruta específica, incluyendo su trazado y paradas.")
    public ResponseEntity<RouteResource> getRouteById(@PathVariable Long routeId) {
        var query = new GetRouteByIdQuery(routeId);
        var route = routeQueryService.handle(query).orElseThrow();
        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }

    @GetMapping("/campuses")
    @Operation(summary = "Obtener sedes universitarias",
            description = "Devuelve la lista de campus disponibles con sus respectivas coordenadas geográficas.")
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

    @GetMapping("/current-leader")
    @Operation(summary = "Obtener ruta activa del líder",
            description = "Busca y devuelve el anuncio en curso (estado PENDING o ACTIVE) creado por el usuario autenticado.")
    public ResponseEntity<RouteResource> getCurrentLeaderRoute() {
        String leaderIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long leaderId = Long.parseLong(leaderIdStr);

        var route = routeQueryService.findActiveRouteByLeaderId(leaderId);
        return route.map(value -> ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PatchMapping("/{routeId}/status")
    @Operation(summary = "Actualizar el estado de la ruta",
            description = "Modifica el ciclo de vida del viaje (por ejemplo, cambiar de PENDING a ACTIVE al iniciar, o a COMPLETED al finalizar).")
    public ResponseEntity<Void> updateRouteStatus(@PathVariable Long routeId, @RequestParam RouteStatus status) {
        routeCommandService.updateStatus(routeId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{routeId}")
    @Operation(summary = "Eliminar una ruta",
            description = "Cancela y elimina de la base de datos una ruta creada previamente.")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId) {
        routeCommandService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{routeId}/waypoints")
    @Operation(summary = "Eliminar una parada de la ruta",
            description = "Remueve un punto de destino específico de un seguidor y recalcula el trazado para acortar el viaje.")
    public ResponseEntity<RouteResource> removeWaypoint(
            @PathVariable Long routeId,
            @RequestParam Double lat,
            @RequestParam Double lng) {

        var command = new RemoveWaypointCommand(routeId, lat, lng);
        var route = routeCommandService.handle(command).orElseThrow();

        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }

    @GetMapping("/campus/{campus}")
    @Operation(summary = "Obtener catálogo de rutas por sede (Panel del Conductor)",
            description = "Devuelve todas las rutas en estado PENDING para una universidad específica, sin filtros de 500m, para que el taxista pueda elegir.")
    public ResponseEntity<List<RouteResource>> getPendingRoutesByCampus(@PathVariable UniversityCampus campus) {
        var query = new GetAllPendingRoutesByCampusQuery(campus);
        var routes = routeQueryService.handle(query);

        var routeResources = routes.stream()
                .map(RouteResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(routeResources);
    }

    @GetMapping("/current-follower")
    @Operation(summary = "Obtener mi anuncio actual como Seguidor",
            description = "Busca la ruta activa donde el usuario actual se ha unido como pasajero.")
    public ResponseEntity<RouteResource> getCurrentFollowerRoute() {
        String passengerIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long passengerId = Long.parseLong(passengerIdStr);

        var route = routeQueryService.findActiveRouteByPassengerId(passengerId);
        return route.map(value -> ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{routeId}/waypoints")
    @Operation(summary = "Añadir una parada a la ruta",
            description = "El seguidor actual se une a la ruta. Su ID se extrae del token de seguridad para registrarlo como pasajero.")
    public ResponseEntity<RouteResource> addWaypoint(@PathVariable Long routeId, @RequestBody WaypointRequest request) {
        // Extraemos el ID del Seguidor del JWT
        String passengerIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long passengerId = Long.parseLong(passengerIdStr);

        // Pasamos el passengerId al comando
        var command = new AddWaypointCommand(routeId, request.lat(), request.lng(), request.address(), passengerId);
        var route = routeCommandService.handle(command).orElseThrow();

        return ResponseEntity.ok(RouteResourceFromEntityAssembler.toResourceFromEntity(route));
    }
}

// Records para recibir los JSON
record CreateRouteRequest(UniversityCampus campus, String address, Double lat, Double lng) {}
record WaypointRequest(Double lat, Double lng, String address) {}