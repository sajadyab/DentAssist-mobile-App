package com.example.dentassist.models;

import java.util.List;

public class WeekDayData {
    private String dateYmd;       // "2026-04-14"
    private String displayDate;   // "Apr 14"
    private String displayDay;    // "MON"
    private List<CalendarSlot> slots;

    public WeekDayData(String dateYmd, String displayDate, String displayDay, List<CalendarSlot> slots) {
        this.dateYmd = dateYmd;
        this.displayDate = displayDate;
        this.displayDay = displayDay;
        this.slots = slots;
    }

    public String getDateYmd() { return dateYmd; }
    public String getDisplayDate() { return displayDate; }
    public String getDisplayDay() { return displayDay; }
    public List<CalendarSlot> getSlots() { return slots; }
}