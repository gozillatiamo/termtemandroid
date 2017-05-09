package com.worldwidewealth.termtem.model;

/**
 * Created by user on 09-May-17.
 */

public class ServiceProRequestModel extends DataRequestModel {

    public static final int SCODE_TOPUP = 1;
    public static final int SCODE_EPIN = 3;

    private int SCODE;

    public ServiceProRequestModel(int scode){
        this.SCODE = scode;
    }

}
