package com.overcode250204.smartlogicticssystem.routing;

import com.overcode250204.smartlogicticssystem.routing.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class RoutingEngine {

    private final RestClient osrmClient = RestClient.builder().baseUrl("http://localhost:5000").build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RoutePlanSolution executeRouting(DepotSetting depot, List<Driver> drivers, List<OrderPlaning> orderPlanings) {

        // Lấy Ma trận từ osrm  (key là id -> id: value là thời gian

        Map<String, Double> distanceMatrix = new HashMap<>();

        StringBuilder coordsBuilder = new StringBuilder();
        coordsBuilder.append(depot.getLocation().getX()).append(",").append(depot.getLocation().getY());

        List<Object> allPoints = new ArrayList<>();
        allPoints.add(depot);

        for (OrderPlaning orderPlaning : orderPlanings) {
            coordsBuilder.append(";").append(orderPlaning.getLocation().getX()).append(",").append(orderPlaning.getLocation().getY());
            allPoints.add(orderPlaning);
        }
        String url = coordsBuilder.toString();
        String fullUrl =
                "http://localhost:5000/table/v1/driving/"
                        + url
                        + "?annotations=duration";

        String response = null;
        try {
            response = osrmClient.get()
                    .uri("/table/v1/driving/" + url + "?annotations=duration")
                    .header("Accept-Encoding", "identity")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parse ma trận từ JSON kết quả của OSRM
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode durations = root.path("durations");

            for (int i = 0; i < allPoints.size(); i++) {
                for (int j = 0; j < allPoints.size(); j++) {
                    double durationInSeconds = durations.get(i).get(j).asDouble();

                    // Lấy  id tương ứng tại vì lưu tất cả id nên sẽ kh biết id thuộc instance nào nên cần check
                    Long fromId = (allPoints.get(i) instanceof DepotSetting) ? ((DepotSetting) allPoints.get(i)).getId() : ((OrderPlaning) allPoints.get(i)).getId();
                    Long toId = (allPoints.get(j) instanceof DepotSetting) ? ((DepotSetting) allPoints.get(j)).getId() : ((OrderPlaning) allPoints.get(j)).getId();

                    // Lưu mapping: "id-id" -> thời giạn di chuyển
                    distanceMatrix.put(fromId + "-" + toId, durationInSeconds);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý ma trận từ OSRM API", e);
        }

        // Chạy timefold

        RoutePlanSolution problem = new RoutePlanSolution(depot, drivers, orderPlanings, new DistanceMatrix(distanceMatrix));

        SolverFactory<RoutePlanSolution> solverFactory = SolverFactory.createFromXmlResource("solverConfig.xml");
        Solver<RoutePlanSolution> solver = solverFactory.buildSolver();


        RoutePlanSolution solvedSolution = solver.solve(problem);


        return solvedSolution;
    }
}