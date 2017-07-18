package com.worldwidewealth.termtem.model;

/**
 * Created by user on 17-Jul-17.
 */

public class LoadBillRefRequest extends DataRequestModel {
    private String BILL_SERVICE_ID;

    public LoadBillRefRequest(String billpay_service_id){
        this.BILL_SERVICE_ID = billpay_service_id;
    }
}
