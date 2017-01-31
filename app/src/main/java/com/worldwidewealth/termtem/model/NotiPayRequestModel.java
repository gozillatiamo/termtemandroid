package com.worldwidewealth.termtem.model;

/**
 * Created by user on 16-Dec-16.
 */

public class NotiPayRequestModel extends DataRequestModel{
    private String AMT;
    private long DATE;
    private String IMAGE;
    private String FROMBANK;
    private String TOBANK;

    public NotiPayRequestModel(String AMT, long DATE, String IMAGE, String FROMBANK, String TOBANK) {
        this.AMT = AMT;
        this.DATE = DATE;
        this.IMAGE = IMAGE;
        this.FROMBANK = FROMBANK;
        this.TOBANK = TOBANK;
    }
}
