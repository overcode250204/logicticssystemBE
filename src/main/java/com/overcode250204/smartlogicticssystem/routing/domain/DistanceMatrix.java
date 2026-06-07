package com.overcode250204.smartlogicticssystem.routing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanceMatrix {

    private Map<String, Double> matrix;

    public double getDistance(Object from, Object to) {

        Long fromId = (from instanceof DepotSetting)
                ? ((DepotSetting) from).getId()
                : ((OrderPlaning) from).getId();

        Long toId = (to instanceof DepotSetting)
                ? ((DepotSetting) to).getId()
                : ((OrderPlaning) to).getId();

        if (fromId.equals(toId)) {
            return 0.0;
        }

        return matrix.getOrDefault(
                fromId + "-" + toId,
                999999.0
        );
    }
}