package com.example.dentassist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardResponse {
    private boolean success;
    private String message;
    private DashboardData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public DashboardData getData() { return data; }

    public static class DashboardData {
        private PatientInfo patient;
        private StatsInfo stats;
        private NextAppointment next_appointment;
        private List<RecentAppointment> recent_appointments;
        private SettingsInfo settings;

        public PatientInfo getPatient() { return patient; }
        public StatsInfo getStats() { return stats; }
        public NextAppointment getNext_appointment() { return next_appointment; }
        public List<RecentAppointment> getRecent_appointments() { return recent_appointments; }
        public SettingsInfo getSettings() { return settings; }
    }

    public static class PatientInfo {
        private String full_name;
        private String member_since;
        private String last_visit;
        private int points;
        private int points_to_next_reward;
        private String referral_code;
        private String subscription_type;
        private String subscription_end_date;

        public String getFull_name() { return full_name; }
        public String getMember_since() { return member_since; }
        public String getLast_visit() { return last_visit; }
        public int getPoints() { return points; }
        public int getPoints_to_next_reward() { return points_to_next_reward; }
        public String getReferral_code() { return referral_code; }
        public String getSubscription_type() { return subscription_type; }
        public String getSubscription_end_date() { return subscription_end_date; }
    }

    public static class StatsInfo {
        private int total_visits;
        private int points;
        private int referrals;
        private String subscription;

        public int getTotal_visits() { return total_visits; }
        public int getPoints() { return points; }
        public int getReferrals() { return referrals; }
        public String getSubscription() { return subscription; }
    }

    public static class NextAppointment {
        private int id;
        private String appointment_date;
        private String appointment_time;
        private int duration;
        private String treatment_type;
        private String status;
        private String doctor_name;

        public int getId() { return id; }
        public String getAppointment_date() { return appointment_date; }
        public String getAppointment_time() { return appointment_time; }
        public int getDuration() { return duration; }
        public String getTreatment_type() { return treatment_type; }
        public String getStatus() { return status; }
        public String getDoctor_name() { return doctor_name; }
    }

    public static class RecentAppointment {
        private int id;
        private String appointment_date;
        private String appointment_time;
        private String treatment_type;
        private String doctor_name;
        private String status;

        public int getId() { return id; }
        public String getAppointment_date() { return appointment_date; }
        public String getAppointment_time() { return appointment_time; }
        public String getTreatment_type() { return treatment_type; }
        public String getDoctor_name() { return doctor_name; }
        public String getStatus() { return status; }
    }

    public static class SettingsInfo {
        private boolean show_points;
        private boolean show_referrals;
        private boolean show_subscription;

        public boolean isShow_points() { return show_points; }
        public boolean isShow_referrals() { return show_referrals; }
        public boolean isShow_subscription() { return show_subscription; }
    }
}