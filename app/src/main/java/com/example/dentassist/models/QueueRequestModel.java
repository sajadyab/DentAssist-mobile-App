package com.example.dentassist.models;

public class QueueRequestModel {
    private int doctor_id;
    private String preferred_date;
    private String treatment_type;
    private String priority;
    private String notes;

    public QueueRequestModel(int doctor_id, String preferred_date, String treatment_type, String priority, String notes) {
        this.doctor_id = doctor_id;
        this.preferred_date = preferred_date;
        this.treatment_type = treatment_type;
        this.priority = priority;
        this.notes = notes;
    }
}