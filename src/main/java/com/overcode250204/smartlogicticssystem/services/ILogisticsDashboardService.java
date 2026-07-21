package com.overcode250204.smartlogicticssystem.services;

import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;

public interface ILogisticsDashboardService {
    LogisticsDashboardResponse getStats(LogisticsDashboardTimeFilter timeFilter);
}
