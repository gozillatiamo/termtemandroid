package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 23/11/2559.
 */

public class SalerptRequestModel extends DataRequestModel {
    private String FROM;
    private String TO;
    private String TYPE;

    public SalerptRequestModel(String FROM, String TO, String TYPE) {
        this.FROM = FROM;
        this.TO = TO;
        this.TYPE = TYPE;
    }
}
