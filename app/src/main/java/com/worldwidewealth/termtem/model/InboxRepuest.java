package com.worldwidewealth.termtem.model;

/**
 * Created by user on 14-Feb-17.
 */

public class InboxRepuest extends DataRequestModel{
    private int PAGE;
    private long FROM;
    private long TO;
    private String TEXT;

    public InboxRepuest(int page, long from, long to, String text){
        this.PAGE = page;
        this.FROM = from;
        this.TO = to;
        this.TEXT = text;
    }
}
