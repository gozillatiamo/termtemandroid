package com.worldwidewealth.termtem.model;

import java.io.Serializable;

/**
 * Created by user on 26-Jan-17.
 */

public class UserMenuModel implements Serializable {
    public static final String KEY_MODEL = "usermenu";
    private String BUTTON;
    private String STATUS;

    public String getBUTTON() {
        return BUTTON;
    }

    public String getSTATUS() {
        return STATUS;
    }
}
