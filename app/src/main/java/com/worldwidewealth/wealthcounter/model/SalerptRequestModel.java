package com.worldwidewealth.wealthcounter.model;

/**
 * Created by MyNet on 23/11/2559.
 */

public class SalerptRequestModel extends DataRequestModel {
    private String FROM;
    private String TO;

    public SalerptRequestModel(String FROM, String TO) {
        this.FROM = FROM;
        this.TO = TO;
    }
}
