package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 22/11/2559.
 */

public class EslipRequestModel extends DataRequestModel {
    private String TRANID;
    private String PHONENO;

    public EslipRequestModel(String TRANID, String PHONENO) {
        this.TRANID = TRANID;
        this.PHONENO = PHONENO;
    }
}
