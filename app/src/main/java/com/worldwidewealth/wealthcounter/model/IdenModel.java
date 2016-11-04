package com.worldwidewealth.wealthcounter.model;

/**
 * Created by MyNet on 4/11/2559.
 */

public class IdenModel {
    private String username = "anonymous";
    private String password = "anonymous";
    private String TIN;

    public IdenModel(String TIN) {
        this.TIN = TIN;
    }
}
