package com.overcode250204.smartlogicticssystem.vrp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OsrmRoutingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OSRM_TABLE_API = "http://router.project-osrm.org/table/v1/driving/";

    public double[][] getDistanceMatrix(List<DeliveryLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            return new double[0][0];
        }

        StringBuilder coordinates = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            DeliveryLocation loc = locations.get(i);
            coordinates.append(loc.getLon()).append(",").append(loc.getLat());
            if (i < locations.size() - 1) {
                coordinates.append(";");
            }
        }

        String url = OSRM_TABLE_API + coordinates.toString() + "?annotations=distance";
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "Ok".equals(response.get("code"))) {
                List<List<Number>> distances = (List<List<Number>>) response.get("distances");
                double[][] matrix = new double[locations.size()][locations.size()];
                for (int i = 0; i < distances.size(); i++) {
                    for (int j = 0; j < distances.get(i).size(); j++) {
                        Number dist = distances.get(i).get(j);
                        matrix[i][j] = dist != null ? dist.doubleValue() : Double.MAX_VALUE;
                    }
                }
                return matrix;
            }
        } catch (Exception e) {
            log.error("Failed to fetch distance matrix from OSRM: ", e);
        }

        // Fallback to Haversine if OSRM fails
        log.warn("Falling back to Haversine distance matrix");
        double[][] matrix = new double[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                matrix[i][j] = calculateHaversineDistance(locations.get(i), locations.get(j));
            }
        }
        return matrix;
    }

    public double[][] getDurationMatrix(List<DeliveryLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            return new double[0][0];
        }

        StringBuilder coordinates = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            DeliveryLocation loc = locations.get(i);
            coordinates.append(loc.getLon()).append(",").append(loc.getLat());
            if (i < locations.size() - 1) {
                coordinates.append(";");
            }
        }

        String url = OSRM_TABLE_API + coordinates.toString() + "?annotations=duration";
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "Ok".equals(response.get("code"))) {
                List<List<Number>> durations = (List<List<Number>>) response.get("durations");
                double[][] matrix = new double[locations.size()][locations.size()];
                for (int i = 0; i < durations.size(); i++) {
                    for (int j = 0; j < durations.get(i).size(); j++) {
                        Number dur = durations.get(i).get(j);
                        matrix[i][j] = dur != null ? dur.doubleValue() : Double.MAX_VALUE;
                    }
                }
                return matrix;
            }
        } catch (Exception e) {
            log.error("Failed to fetch duration matrix from OSRM: ", e);
        }

        // Fallback using Haversine distance divided by 30 km/h (8.33 m/s) average speed
        log.warn("Falling back to Haversine duration matrix");
        double[][] matrix = new double[locations.size()][locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                double dist = calculateHaversineDistance(locations.get(i), locations.get(j));
                matrix[i][j] = dist / 8.33;
            }
        }
        return matrix;
    }

    public double getTravelDurationSeconds(double lat1, double lon1, double lat2, double lon2) {
        String url = "http://router.project-osrm.org/route/v1/driving/" + lon1 + "," + lat1 + ";" + lon2 + "," + lat2 + "?overview=false";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "Ok".equals(response.get("code"))) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                if (routes != null && !routes.isEmpty()) {
                    Number duration = (Number) routes.get(0).get("duration");
                    return duration != null ? duration.doubleValue() : 0.0;
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch route duration from OSRM: ", e);
        }
        // Fallback using Haversine distance / 50 km/h (13.88 m/s) average speed
        double distanceMeters = calculateHaversineDistance(
                new DeliveryLocation(0L, lat1, lon1), 
                new DeliveryLocation(0L, lat2, lon2)
        );
        return distanceMeters / 13.88;
    }
    
    private double calculateHaversineDistance(DeliveryLocation loc1, DeliveryLocation loc2) {
        final int R = 6371000; // Radius of the earth in meters
        double latDistance = Math.toRadians(loc2.getLat() - loc1.getLat());
        double lonDistance = Math.toRadians(loc2.getLon() - loc1.getLon());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc1.getLat())) * Math.cos(Math.toRadians(loc2.getLat()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
