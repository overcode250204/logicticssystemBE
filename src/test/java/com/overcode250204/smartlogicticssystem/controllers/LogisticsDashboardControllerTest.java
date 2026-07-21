package com.overcode250204.smartlogicticssystem.controllers;

import com.overcode250204.smartlogicticssystem.dtos.response.LogisticsDashboardResponse;
import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;
import com.overcode250204.smartlogicticssystem.exception.GlobalExceptionHandler;
import com.overcode250204.smartlogicticssystem.services.ILogisticsDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogisticsDashboardControllerTest {

    private ILogisticsDashboardService dashboardService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        dashboardService = mock(ILogisticsDashboardService.class);
        LogisticsDashboardController controller = new LogisticsDashboardController(dashboardService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getStatsAcceptsTimeFilterUsedByFlutter() throws Exception {
        LogisticsDashboardResponse response = LogisticsDashboardResponse.builder()
                .totalOrders(12)
                .successRate(75.0)
                .build();
        when(dashboardService.getStats(LogisticsDashboardTimeFilter.MONTH)).thenReturn(response);

        mockMvc.perform(get("/api/admin/logistics-dashboard/stats")
                        .queryParam("timeFilter", "MONTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrders").value(12))
                .andExpect(jsonPath("$.data.successRate").value(75.0));

        verify(dashboardService).getStats(LogisticsDashboardTimeFilter.MONTH);
    }

    @Test
    void getStatsDefaultsToMonthWhenFilterIsMissing() throws Exception {
        when(dashboardService.getStats(LogisticsDashboardTimeFilter.MONTH))
                .thenReturn(LogisticsDashboardResponse.builder().build());

        mockMvc.perform(get("/api/admin/logistics-dashboard/stats"))
                .andExpect(status().isOk());

        verify(dashboardService).getStats(LogisticsDashboardTimeFilter.MONTH);
    }

    @Test
    void getStatsReturnsBadRequestForUnsupportedFilter() throws Exception {
        mockMvc.perform(get("/api/admin/logistics-dashboard/stats")
                        .queryParam("timeFilter", "YEAR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(
                        "Invalid value for request parameter 'timeFilter'"
                ));
    }
}
