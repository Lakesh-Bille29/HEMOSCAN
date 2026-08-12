package com.example.brainhemorrhage.api;

public class LoginResponse extends BaseResponse {
    private User user;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public static class User {
        private String name;
        private String email;
        private String mobile;
        private String gender;
        private String specialty;
        private String profile_image;
        private String bio;
        private String hospital;
        private String license;
        private int years_exp;
        private int dark_mode;
        private String language;
        private int daily_summary;
        private int sound;
        private int vibration;
        private int theme_mode;

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getMobile() { return mobile; }
        public String getGender() { return gender; }
        public String getSpecialty() { return specialty; }
        public String getProfile_image() { return profile_image; }
        public String getBio() { return bio; }
        public String getHospital() { return hospital; }
        public String getLicense() { return license; }
        public int getYears_exp() { return years_exp; }
        public int getDark_mode() { return dark_mode; }
        public String getLanguage() { return language; }
        public int getDaily_summary() { return daily_summary; }
        public int getSound() { return sound; }
        public int getVibration() { return vibration; }
        public int getTheme_mode() { return theme_mode; }
    }
}
