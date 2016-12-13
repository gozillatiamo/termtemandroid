package com.worldwidewealth.wealthwallet.model;

/**
 * Created by MyNet on 15/11/2559.
 */

public class ChangePasswordRequestModel extends DataRequestModel {
    private String NEWPASSWORD;

    public ChangePasswordRequestModel(String newpassword){
        this.NEWPASSWORD = newpassword;
    }
}
