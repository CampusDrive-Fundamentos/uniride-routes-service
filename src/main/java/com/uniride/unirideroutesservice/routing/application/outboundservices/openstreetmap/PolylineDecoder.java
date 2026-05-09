package com.uniride.unirideroutesservice.routing.application.outboundservices.openstreetmap;

import com.uniride.unirideroutesservice.routing.domain.model.valueobjects.Location;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolylineDecoder {

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

    // NUEVO: Convierte los puntos en una Línea Geográfica de PostGIS
    public LineString decodeToLineString(String encodedPath) {
        List<Location> path = decodeToList(encodedPath);
        Coordinate[] coords = new Coordinate[path.size()];

        for (int i = 0; i < path.size(); i++) {
            // Nota: En geometría JTS, el orden es X (Longitud), Y (Latitud)
            coords[i] = new Coordinate(path.get(i).getLongitude(), path.get(i).getLatitude());
        }

        // SRID 4326 es el estándar mundial del GPS
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createLineString(coords);
    }

    // NUEVO: Lo usamos ÚNICAMENTE para calcular los kilómetros del estudiante para el microservicio de Finance.
    public double calculatePointToPointDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la tierra en KM
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}