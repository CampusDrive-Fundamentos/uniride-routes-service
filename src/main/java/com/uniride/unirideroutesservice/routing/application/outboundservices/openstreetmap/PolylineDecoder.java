package com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolylineDecoder {

    // 1. Decodifica el texto que envía OpenRouteService a una lista de puntos reales
    public List<Location> decodeToList(String encodedPath) {
        int len = encodedPath.length();
        int index = 0;
        int lat = 0;
        int lng = 0;
        List<Location> path = new ArrayList<>();

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPath.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedPath.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            path.add(new Location((double) lat / 1E5, (double) lng / 1E5, "Ruta"));
        }
        return path;
    }

    // 2. La famosa Fórmula de Haversine (calcula distancias curvos en la tierra)
    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la tierra en KM
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Devuelve la distancia en Kilómetros
    }

    // 3. Evalúa si el estudiante Seguidor está dentro del tubo de 500m
    public boolean isPointNearLine(List<Location> polyline, double studentLat, double studentLng) {
        for (Location point : polyline) {
            double distance = calculateHaversineDistance(studentLat, studentLng, point.getLatitude(), point.getLongitude());
            if (distance <= 0.5) { // 0.5 KM = 500 metros
                return true;
            }
        }
        return false;
    }
}