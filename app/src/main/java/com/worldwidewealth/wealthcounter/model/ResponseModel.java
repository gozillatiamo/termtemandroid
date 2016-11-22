package com.worldwidewealth.wealthcounter.model;

import com.google.gson.annotations.Expose;

/**
 * Created by MyNet on 8/11/2559.
 */

public class ResponseModel{
    private int Status;
    private String Msg;
    private String TXID;
    private String ff;

    public String getFf() {
        return ff;
    }

    public void setFf(String ff) {
        this.ff = ff;
    }

    public String getMsg() {
        return Msg;
    }


    public String getTXID() {
        return TXID;
    }

    public int getStatus() {
        return Status;
    }
}
