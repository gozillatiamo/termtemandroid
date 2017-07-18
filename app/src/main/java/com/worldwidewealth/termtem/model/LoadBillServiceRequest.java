package com.worldwidewealth.termtem.model;

/**
 * Created by user on 17-Jul-17.
 */

public class LoadBillServiceRequest extends DataRequestModel {
    private String BILLPAY_CATEGORY_ID;

    public LoadBillServiceRequest(String billpay_category_id){
        this.BILLPAY_CATEGORY_ID = billpay_category_id;
    }
}
