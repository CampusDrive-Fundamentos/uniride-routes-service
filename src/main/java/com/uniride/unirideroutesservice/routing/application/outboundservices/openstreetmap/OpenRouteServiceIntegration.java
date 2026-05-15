package com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OpenRouteServiceIntegration {

    @Value("${ors.api.key}")
    private String apiKey;

    private final String ORS_DIRECTIONS_URL = "https://api.openrouteservice.org/v2/directions/driving-car";

    // Calcula la ruta óptima pasando por Origen -> Todas las Paradas (orden optimizado) -> Destino
    public Map<String, Object> calculateOptimalRoute(Location start, Location end, List<Location> waypoints) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        // Armamos el JSON con las coordenadas. Formato ORS: [Longitud, Latitud]
        List<double[]> coordinates = new java.util.ArrayList<>();
        coordinates.add(new double[]{start.getLongitude(), start.getLatitude()});

        for (Location w : waypoints) {
            coordinates.add(new double[]{w.getLongitude(), w.getLatitude()});
        }
        coordinates.add(new double[]{end.getLongitude(), end.getLatitude()});

        Map<String, Object> body = new HashMap<>();
        body.put("coordinates", coordinates);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map response = restTemplate.postForObject(ORS_DIRECTIONS_URL, entity, Map.class);
            List routes = (List) response.get("routes");
            Map routeData = (Map) routes.get(0);

            Map<String, Object> result = new HashMap<>();
            result.put("geometry", routeData.get("geometry")); // El Polyline

            Map summary = (Map) routeData.get("summary");
            result.put("distanceKm", ((Number) summary.get("distance")).doubleValue() / 1000.0);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error calculando ruta con OpenRouteService", e);
        }
    }
}