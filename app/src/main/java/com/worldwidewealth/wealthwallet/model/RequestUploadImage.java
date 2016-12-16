package com.worldwidewealth.wealthwallet.model;

/**
 * Created by user on 16-Dec-16.
 */

public class RequestUploadImage extends DataRequestModel{
    private String REF;
    private String AMT;
    private long DATE;
    private String IMAGE;

    public RequestUploadImage(String REF, String AMT, long DATE, String IMAGE) {
        this.REF = REF;
        this.AMT = AMT;
        this.DATE = DATE;
        this.IMAGE = IMAGE;
    }
}
