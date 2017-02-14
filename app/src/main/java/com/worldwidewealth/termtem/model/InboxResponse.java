package com.worldwidewealth.termtem.model;

import java.util.Date;

/**
 * Created by user on 14-Feb-17.
 */

public class InboxResponse {
    private String msgid;
    private String title;
    private String msg;
    private boolean Readed;
    private Date Create_Date;

    public String getMsgid() {
        return msgid;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isReaded() {
        return Readed;
    }

    public void setReaded(boolean readed) {
        Readed = readed;
    }

    public Date getCreate_Date() {
        return Create_Date;
    }
}
