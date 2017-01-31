package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 16/11/2559.
 */

public class TopupPreviewRequestModel extends DataRequestModel {

    private String CARRIER;
    private double AMT;

    public TopupPreviewRequestModel(double AMT, String CARRIER) {
        this.AMT = AMT;
        this.CARRIER = CARRIER;
    }
}
