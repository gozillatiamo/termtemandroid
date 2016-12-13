package com.worldwidewealth.wealthwallet.model;

import java.util.Date;

/**
 * Created by MyNet on 24/11/2559.
 */

public class SalerptResponseModel {
    private String PAYCODE;
    private double AMOUNT;
    private double COMM_AMT;
    private double CHECKTOTAL;
    private Date PAYMENT_DATE;
    private String BILLER;
    private String TYPE;
    private String PHONENO;

    public String getBILLER() {
        return BILLER;
    }

    public void setBILLER(String BILLER) {
        this.BILLER = BILLER;
    }

    public String getPHONENO() {
        return PHONENO;
    }

    public void setPHONENO(String PHONENO) {
        this.PHONENO = PHONENO;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public double getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(double AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public double getCHECKTOTAL() {
        return CHECKTOTAL;
    }

    public void setCHECKTOTAL(double CHECKTOTAL) {
        this.CHECKTOTAL = CHECKTOTAL;
    }

    public double getCOMM_AMT() {
        return COMM_AMT;
    }

    public void setCOMM_AMT(double COMM_AMT) {
        this.COMM_AMT = COMM_AMT;
    }

    public String getPAYCODE() {
        return PAYCODE;
    }

    public void setPAYCODE(String PAYCODE) {
        this.PAYCODE = PAYCODE;
    }

    public Date getPAYMENT_DATE() {
        return PAYMENT_DATE;
    }

    public void setPAYMENT_DATE(Date PAYMENT_DATE) {
        this.PAYMENT_DATE = PAYMENT_DATE;
    }

}
