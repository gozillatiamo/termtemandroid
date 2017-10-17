package com.worldwidewealth.termtem.model;

/**
 * Created by gozillatiamo on 7/19/17.
 */

public class PreviewBillRequest extends DataRequestModel {
    private String BARCODE1;
    private String BARCODE2 = "";
    private String BILL_SERVICE_CODE;
    private String BILL_SERVICE_ID;
    private String PHONENO;

    public PreviewBillRequest(String barcode, String bill_service_code, String bill_service_id, String phoneNo){
        this.BARCODE1 = barcode;
        this.BILL_SERVICE_CODE = bill_service_code;
        this.BILL_SERVICE_ID = bill_service_id;
        this.PHONENO = phoneNo;
    }

}
