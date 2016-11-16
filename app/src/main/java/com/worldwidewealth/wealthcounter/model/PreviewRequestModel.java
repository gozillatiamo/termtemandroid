package com.worldwidewealth.wealthcounter.model;

/**
 * Created by MyNet on 16/11/2559.
 */

public class PreviewRequestModel extends DataRequestModel {

    private String CARRIER;
    private double AMT;

    public PreviewRequestModel(double AMT, String CARRIER) {
        this.AMT = AMT;
        this.CARRIER = CARRIER;
    }
}
