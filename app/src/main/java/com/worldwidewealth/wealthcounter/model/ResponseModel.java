package com.worldwidewealth.wealthcounter.model;

import com.google.gson.annotations.Expose;

/**
 * Created by MyNet on 8/11/2559.
 */

public class ResponseModel{
    private String Status;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
