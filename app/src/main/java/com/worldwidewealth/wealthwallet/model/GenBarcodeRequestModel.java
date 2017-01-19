package com.worldwidewealth.wealthwallet.model;

/**
 * Created by MyNet on 22/11/2559.
 */

public class GenBarcodeRequestModel extends DataRequestModel {
    private String AMT;

    public GenBarcodeRequestModel(String AMT) {
        this.AMT = AMT;
    }
}
