package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 8/11/2559.
 */

public class ResponseModel{

    private int Status;
    private String Msg;
    private String TXID;
    private String ff;
    private String version;
    private int show;
    private String desc;

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

    public String getVersion() {
        return version;
    }

    public int getStatus() {
        return Status;
    }

    public int getShow() {
        return show;
    }

    public String getDesc() {
        return desc;
    }
}
