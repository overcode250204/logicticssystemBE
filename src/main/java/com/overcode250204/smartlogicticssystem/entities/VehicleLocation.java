package com.overcode250204.smartlogicticssystem.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleLocation {

    private String id;

    private double lat;

    private double lng;

    private double heading;
}