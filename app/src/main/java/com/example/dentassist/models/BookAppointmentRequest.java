package com.example.dentassist.models;

public class BookAppointmentRequest {
    private int doctor_id;
    private String appointment_date;
    private String appointment_time;
    private String treatment_type;
    private String notes;

    public BookAppointmentRequest(int doctor_id, String appointment_date,
                                  String appointment_time, String treatment_type, String notes) {
        this.doctor_id = doctor_id;
        this.appointment_date = appointment_date;
        this.appointment_time = appointment_time;
        this.treatment_type = treatment_type;
        this.notes = notes;
    }
}