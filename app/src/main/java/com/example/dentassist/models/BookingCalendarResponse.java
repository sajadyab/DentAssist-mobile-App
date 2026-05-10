package com.example.dentassist.models;

import java.util.List;

public class BookingCalendarResponse {
    private boolean success;
    private String message;
    private CalendarData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public CalendarData getData() { return data; }

    public static class CalendarData {
        private List<DoctorInfo> doctors;
        private List<VisitTypeInfo> visit_types;
        private List<WeekDayInfo> week_days;
        private int slot_duration;
        private String week_start;
        private String week_end;
        private List<PendingRequest> pending_requests;

        public List<DoctorInfo> getDoctors() { return doctors; }
        public List<VisitTypeInfo> getVisit_types() { return visit_types; }
        public List<WeekDayInfo> getWeek_days() { return week_days; }
        public int getSlot_duration() { return slot_duration; }
        public String getWeek_start() { return week_start; }
        public String getWeek_end() { return week_end; }
        public List<PendingRequest> getPending_requests() { return pending_requests; }

        // ✅ PendingRequest INSIDE CalendarData
        public static class PendingRequest {
            private int id;
            private String doctor_name;
            private String requested_date;
            private String requested_time;
            private String treatment_type;
            private String description;
            private String created_at;

            public int getId() { return id; }
            public String getDoctor_name() { return doctor_name; }
            public String getRequested_date() { return requested_date; }
            public String getRequested_time() { return requested_time; }
            public String getTreatment_type() { return treatment_type; }
            public String getDescription() { return description; }
            public String getCreated_at() { return created_at; }
        }
    }

    public static class DoctorInfo {
        private int id;
        private String full_name;
        public int getId() { return id; }
        public String getFull_name() { return full_name; }
        @Override public String toString() { return full_name; }
    }

    public static class VisitTypeInfo {
        private int id;
        private String name;
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public String toString() { return name; }
    }

    public static class WeekDayInfo {
        private String date_ymd;
        private String display_day;
        private String display_date;
        private List<SlotInfo> slots;

        public String getDate_ymd() { return date_ymd; }
        public String getDisplay_day() { return display_day; }
        public String getDisplay_date() { return display_date; }
        public List<SlotInfo> getSlots() { return slots; }
    }

    public static class SlotInfo {
        private String time;
        private String label;
        private String state;

        public String getTime() { return time; }
        public String getLabel() { return label; }
        public String getState() { return state; }
    }
}