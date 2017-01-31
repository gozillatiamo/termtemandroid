package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 4/11/2559.
 */

public class PreRequestModel {
    private String action;
    private Data data;
    public PreRequestModel(String action, Data data) {
        this.action = action;
        this.data = data;
    }

    public static class Data{
        private String PUSHTOKEN;
        private String DEVICEID;
        private double LAT;
        private double LONG;
        private String PLATFORM;
        private String AGENTID = "";
        private String USERID = "";

        public Data(String token, String device_id, double latitude, double longitude, String platfrom) {
            this.PUSHTOKEN = token;
            this.DEVICEID = device_id;
            this.LAT = latitude;
            this.LONG = longitude;
            this.PLATFORM = platfrom;
        }
    }
}
