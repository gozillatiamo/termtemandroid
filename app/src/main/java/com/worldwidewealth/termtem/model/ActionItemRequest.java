package com.worldwidewealth.termtem.model;

/**
 * Created by user on 14-Feb-17.
 */

public class ActionItemRequest extends DataRequestModel {
    private String MSGID;
    private String FAVID;

    public ActionItemRequest(String msgid, String favid){
        this.MSGID = msgid;
        this.FAVID = favid;
    }

    public static ActionItemRequest MSG(String msgid){
        return new ActionItemRequest(msgid, null);
    }

    public static ActionItemRequest FAV(String favid){
        return new ActionItemRequest(null, favid);
    }
}
