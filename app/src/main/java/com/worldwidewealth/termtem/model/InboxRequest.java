package com.worldwidewealth.termtem.model;

/**
 * Created by user on 14-Feb-17.
 */

public class InboxRequest extends DataRequestModel{

    public static final int TYPE_ALL = 1;
    public static final int TYPE_TEXT = 4;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;

    private int PAGE;
    private long FROM;
    private long TO;
    private String TEXT;
    private int TYPE;

    public InboxRequest(int page, long from, long to, String text, int type){
        this.PAGE = page;
        this.FROM = from;
        this.TO = to;
        this.TEXT = text;
        this.TYPE = type;
    }
}
