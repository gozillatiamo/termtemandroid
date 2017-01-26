package com.worldwidewealth.wealthwallet.model;

import java.io.Serializable;

/**
 * Created by user on 26-Jan-17.
 */

public class UserMenuModel implements Serializable {
    public static final String KEY_MODEL = "usermenu";
    public static final String SHOW = "SHOW";
    public static final String DISABLE = "DISABLE";
    public static final String HIDE = "HIDE";
    public static final String CASHIN_BUTTON = "CASHIN";
    public static final String SCAN_BUTTON = "SCAN";
    public static final String TOPUP_BUTTON = "TOPUP";
    public static final String SETUP_BUTTON = "SETUP";
    public static final String SUPPORT_BUTTON = "SUPPORT";
    public static final String NOTIPAY_BUTTON = "NOTIPAY";
    public static final String HISTORY_BUTTON = "HISTORY";
    public static final String QR_BUTTON = "QR";
    private String BUTTON;
    private String STATUS;

    public String getBUTTON() {
        return BUTTON;
    }

    public String getSTATUS() {
        return STATUS;
    }
}
