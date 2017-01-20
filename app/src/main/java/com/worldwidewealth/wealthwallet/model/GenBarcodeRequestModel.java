package com.worldwidewealth.wealthwallet.model;

/**
 * Created by MyNet on 22/11/2559.
 */

public class GenBarcodeRequestModel extends DataRequestModel {
    private String AMT;
    private int TYPE;
    public GenBarcodeRequestModel(String AMT, int TYPE) {
        this.AMT = AMT;
        this.TYPE = TYPE;
    }
}
