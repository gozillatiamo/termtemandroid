package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 15/11/2559.
 */

public class LoadButtonRequestModel extends DataRequestModel {
    private String CARRIER;

    public LoadButtonRequestModel(String carrier) {
       this.CARRIER = carrier;
    }
}
