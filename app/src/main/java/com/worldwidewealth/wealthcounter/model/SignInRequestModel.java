package com.worldwidewealth.wealthcounter.model;

/**
 * Created by MyNet on 8/11/2559.
 */

public class SignInRequestModel {
    private String action = "LOGIN";
    private Data data;

    public String getAction() {
        return action;
    }

    public SignInRequestModel(SignInRequestModel.Data data){
        this.data = data;
    }

    public static class Data{
        private String DEVICEID;
        private String PLATFORM;
        private String USERNAME;
        private String PASSWORD;
        private String TXID;
        private String AGENTID = "";
        private String USERID = "";


        public Data(String device_id,
                    String platform,
                    String username,
                    String password,
                    String tx_id){
            this.DEVICEID = device_id;
            this.PLATFORM = platform;
            this.USERNAME = username;
            this.PASSWORD = password;
            this.TXID = tx_id;
        }
    }
}
