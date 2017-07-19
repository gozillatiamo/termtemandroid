package com.worldwidewealth.termtem.model;

/**
 * Created by MyNet on 16/11/2559.
 */

public class TopupPreviewResponseModel {
    private double AMOUNT;
    private String COMMISSION_RATE;
    private double COMMISSION_AMOUNT;
    private double BALANCE;
    private double TOTAL;
    private float MARKUP;
    private String TNID;
    private double FEE;
    private double NET;
    private String REF1;

    public double getAMOUNT() {
        return AMOUNT;
    }

    public double getBALANCE() {
        return BALANCE;
    }

    public double getCOMMISSION_AMOUNT() {
        return COMMISSION_AMOUNT;
    }

    public String getCOMMISSION_RATE() {
        return COMMISSION_RATE;
    }

    public double getTOTAL() {
        return TOTAL;
    }

    public float getMARKUP() {
        return MARKUP;
    }

    public String getTNID() {
        return TNID;
    }

    public double getFEE() {
        return FEE;
    }

    public double getNET() {
        return NET;
    }

    public String getREF1() {
        return REF1;
    }
}
