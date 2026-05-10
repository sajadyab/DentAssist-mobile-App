package com.example.dentassist.models;

import java.util.List;

public class ToothChartResponse {
    private boolean success;
    private String message;
    private ToothChartData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ToothChartData getData() { return data; }

    public static class ToothChartData {
        private List<Tooth> teeth;
        private String chart_type;      // "adult" or "primary"
        private String patient_name;

        public List<Tooth> getTeeth() { return teeth; }
        public String getChart_type() { return chart_type; }
        public String getPatient_name() { return patient_name; }
    }

    public static class Tooth {
        private int tooth_number;
        private String status;
        private String diagnosis;
        private String treatment;
        private String notes;
        private String last_updated;

        public int getTooth_number() { return tooth_number; }
        public String getStatus() { return status; }
        public String getDiagnosis() { return diagnosis; }
        public String getTreatment() { return treatment; }
        public String getNotes() { return notes; }
        public String getLast_updated() { return last_updated; }
    }
}