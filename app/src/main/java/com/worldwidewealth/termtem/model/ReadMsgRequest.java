package com.worldwidewealth.termtem.model;

/**
 * Created by user on 14-Feb-17.
 */

public class ReadMsgRequest extends DataRequestModel {
    private String MSGID;

    public ReadMsgRequest(String msgid){
        this.MSGID = msgid;
    }
}
