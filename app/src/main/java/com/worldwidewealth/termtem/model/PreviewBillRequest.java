package com.worldwidewealth.termtem.model;

/**
 * Created by gozillatiamo on 7/19/17.
 */

public class PreviewBillRequest extends DataRequestModel {
    private String BARCODE;
    private String BILL_SERVICE_CODE;
    private String BILL_SERVICE_ID;

    public PreviewBillRequest(String barcode, String bill_service_code, String bill_service_id){
        this.BARCODE = barcode;
        this.BILL_SERVICE_CODE = bill_service_code;
        this.BILL_SERVICE_ID = bill_service_id;
    }

}