package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 15/11/2559.
 */

public class RequestModel {
    private String action;
    private DataRequestModel data;

    public RequestModel(String action, DataRequestModel data){
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public DataRequestModel getData() {
        return data;
    }
}
