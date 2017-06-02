package com.worldwidewealth.termtem.model;

/**
 * Created by gozillatiamo on 5/29/17.
 */

public class VasValuesModel {
    private double PRICE;
    private String SPEED;
    private String VOLUME;
    private int LIMITDAY;

    public double getPRICE() {
        return PRICE;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
    }

    public String getSPEED() {
        return SPEED;
    }

    public void setSPEED(String SPEED) {
        this.SPEED = SPEED;
    }

    public String getVOLUME() {
        return VOLUME;
    }

    public void setVOLUME(String VOLUME) {
        this.VOLUME = VOLUME;
    }

    public int getLIMITDAY() {
        return LIMITDAY;
    }

    public void setLIMITDAY(int LIMITDAY) {
        this.LIMITDAY = LIMITDAY;
    }
}
