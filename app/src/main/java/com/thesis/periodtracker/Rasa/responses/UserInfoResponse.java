package com.thesis.periodtracker.Rasa.responses;


public class UserInfoResponse extends RasaResponse {
    private CustomData custom;

    public CustomData getCustom() {
        return custom;
    }

    public static class CustomData {
        private String control;
        private UserData data;

        public String getControl() {
            return control;
        }

        public UserData getData() {
            return data;
        }
    }

    public static class UserData {
        private String name;
        private int age;
        private boolean isMenopause;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public boolean isMenopause() {
            return isMenopause;
        }
    }
}
