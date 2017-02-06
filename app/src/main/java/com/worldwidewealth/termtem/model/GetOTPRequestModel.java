package com.worldwidewealth.termtem.model;

import com.worldwidewealth.termtem.Global;

/**
 * Created by MyNet on 16/11/2559.
 */

public class GetOTPRequestModel extends DataRequestModel{
    private String PUSHTOKEN = Global.getInstance().getTOKEN();
}
