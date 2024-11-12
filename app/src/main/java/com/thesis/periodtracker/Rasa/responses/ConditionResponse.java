package com.thesis.periodtracker.Rasa.responses;

import com.google.gson.annotations.SerializedName;


public class ConditionResponse extends RasaResponse {
    private CustomData custom;

    public CustomData getCustom() {
        return custom;
    }

    public static class CustomData {
        private String control;
        private ConditionData data;

        public String getControl() {
            return control;
        }

        public ConditionData getData() {
            return data;
        }
    }

    public static class ConditionData {
        private String conditionName;
        private int conditionScore;
        private boolean lifeThreat;

        public String getConditionName() {
            return conditionName;
        }

        public int getConditionScore() {
            return conditionScore;
        }

        public boolean isLifeThreat() {
            return lifeThreat;
        }
    }
}
