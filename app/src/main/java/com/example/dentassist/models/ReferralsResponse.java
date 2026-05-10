package com.example.dentassist.models;

import java.util.List;

public class ReferralsResponse {
    private boolean success;
    private String message;
    private ReferralsData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ReferralsData getData() { return data; }

    public static class ReferralsData {
        private String referral_code;
        private int referral_count;
        private int points_earned;
        private int points_per_referral;
        private List<ReferredFriend> referred_friends;

        public String getReferral_code() { return referral_code; }
        public int getReferral_count() { return referral_count; }
        public int getPoints_earned() { return points_earned; }
        public int getPoints_per_referral() { return points_per_referral; }
        public List<ReferredFriend> getReferred_friends() { return referred_friends; }
    }

    public static class ReferredFriend {
        private String full_name;
        private String email;
        private String phone;
        private String joined_date;

        public String getFull_name() { return full_name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getJoined_date() { return joined_date; }
    }
}