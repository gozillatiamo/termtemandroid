package com.worldwidewealth.wealthcounter.model;

/**
 * Created by MyNet on 8/11/2559.
 */

public class PreResponseModel {
    private int Status;
    private String Msg;
    private String TXID;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getTXID() {
        return TXID;
    }

    public void setTXID(String TXID) {
        this.TXID = TXID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
