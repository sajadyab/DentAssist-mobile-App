package com.example.dentassist.models;
import java.util.List;
public class CalendarResponse {
    private List<WeekDayData> weekDays;
    private List<String> timeRows; // Sorted list of unique "HH:MM:SS" across all days
    private int slotDurationMinutes;
    // getters and setters...
}