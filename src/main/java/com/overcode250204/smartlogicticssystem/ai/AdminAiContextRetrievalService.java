package com.overcode250204.smartlogicticssystem.ai;

import com.overcode250204.smartlogicticssystem.enums.LogisticsDashboardTimeFilter;

public interface AdminAiContextRetrievalService {
    AdminAiContext retrieve(String question, LogisticsDashboardTimeFilter timeFilter);
}
