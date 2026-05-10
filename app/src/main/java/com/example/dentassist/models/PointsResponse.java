package com.example.dentassist.models;

import java.util.List;

public class PointsResponse {
    private boolean success;
    private String message;
    private PointsData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public PointsData getData() { return data; }

    public static class PointsData {
        private int total_points;
        private int points_to_next_reward;
        private List<HistoryItem> history;

        public int getTotal_points() { return total_points; }
        public int getPoints_to_next_reward() { return points_to_next_reward; }
        public List<HistoryItem> getHistory() { return history; }
    }

    public static class HistoryItem {
        private String side;
        private String title;
        private String muted;
        private String pointsLabel;

        public String getSide() { return side; }
        public String getTitle() { return title; }
        public String getMuted() { return muted; }
        public String getPointsLabel() { return pointsLabel; }
    }
}