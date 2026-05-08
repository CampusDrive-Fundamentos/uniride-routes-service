package com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class OpenStreetMapGeocodingService {

    private final String OSM_NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public Location geocodeAddress(String address) {
        RestTemplate restTemplate = new RestTemplate();

        // Cambio aquí: Usamos fromUriString que es más estable en compilación
        String url = UriComponentsBuilder.fromUriString(OSM_NOMINATIM_URL)
                .queryParam("q", address + ", Lima, Peru")
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .toUriString();

        // IMPORTANTE: OpenStreetMap bloquea las peticiones genéricas de Java.
        // Debemos enviar un Header con un User-Agent personalizado.
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "CampusDrive-UniRide-App/1.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Usamos exchange en lugar de getForObject para poder enviar los Headers
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> responseBody = response.getBody();

            if (responseBody != null && !responseBody.isEmpty()) {
                Map<String, Object> locationData = responseBody.get(0);
                Double lat = Double.parseDouble(locationData.get("lat").toString());
                Double lon = Double.parseDouble(locationData.get("lon").toString());
                String displayName = locationData.get("display_name").toString();
                return new Location(lat, lon, displayName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al contactar OpenStreetMap. Verifica tu conexión o la dirección.", e);
        }
        throw new IllegalArgumentException("No se encontraron coordenadas exactas para la dirección: " + address);
    }
}