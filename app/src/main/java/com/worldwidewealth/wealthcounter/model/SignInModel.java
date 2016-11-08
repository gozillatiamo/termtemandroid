package com.worldwidewealth.wealthcounter.model;

/**
 * Created by MyNet on 8/11/2559.
 */

public class SignInModel {
    private String ACTION = "LOGIN";
    private Data DATA;

    public SignInModel(SignInModel.Data data){
        this.DATA = data;
    }

    public static class Data{
        private String DEVICEID;
        private String PLATFORM;
        private String USER;
        private String PASSWORD;
        private String TXID;

        public Data(String device_id,
                    String platform,
                    String user,
                    String password,
                    String tx_id){
            this.DEVICEID = device_id;
            this.PLATFORM = platform;
            this.USER = user;
            this.PASSWORD = password;
            this.TXID = tx_id;
        }
    }
}
