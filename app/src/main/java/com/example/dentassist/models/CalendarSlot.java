package com.example.dentassist.models;

public class CalendarSlot {
    private String timeHis;      // "HH:MM:SS"
    private String label;        // "9:00 AM"
    private SlotState state;
    private boolean isClickable;

    public CalendarSlot(String timeHis, String label, SlotState state) {
        this.timeHis = timeHis;
        this.label = label;
        this.state = state;
        this.isClickable = (state == SlotState.FREE);
    }

    public String getTimeHis() { return timeHis; }
    public String getLabel() { return label; }
    public SlotState getState() { return state; }
    public boolean isClickable() { return isClickable; }
}