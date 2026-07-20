package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.TripDashboardDataResponse;
import com.overcode250204.smartlogicticssystem.dtos.response.TripHistoryDTO;
import org.springframework.data.domain.Page;

public interface ITripDashboardService {
    TripDashboardDataResponse getDashboardData(String type, int month, int year);
    Page<TripHistoryDTO> getTripHistory(String type, int month, int year, String keyword, int page, int size);
}
