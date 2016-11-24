package com.worldwidewealth.wealthcounter.model;

import java.util.Date;

/**
 * Created by MyNet on 24/11/2559.
 */

public class SalerptResponseModel {
    private String TXID;
    private String TRANSACTION_ID;
    private String SESSIONID;
    private String DEVICEID;
    private String AGENTID;
    private String USERID;
    private String PLATFORM;
    private String PAYCODE;
    private double WALLET_BEFORE;
    private String SERVICE_CODE;
    private double AMOUNT;
    private double COMM_AMT;
    private int COMM_INC;
    private double DISCOUNT;
    private double VAT;
    private int VAT_INC;
    private double CHECKTOTAL;
    private Date PAYMENT_DATE;
    private double WALLET_AFTER;
    private int SAVEFLAG;
    private Date CREATED_DATE;
    private String CREATED_BY;
    private Date UPDATR_DATE;
    private String UPDATE_BY;
    private int ACTIVE;
    private String COMM_RATE;
    private double GRANTTOTAL;

    public int getACTIVE() {
        return ACTIVE;
    }

    public void setACTIVE(int ACTIVE) {
        this.ACTIVE = ACTIVE;
    }

    public String getAGENTID() {
        return AGENTID;
    }

    public void setAGENTID(String AGENTID) {
        this.AGENTID = AGENTID;
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

    public int getCOMM_INC() {
        return COMM_INC;
    }

    public void setCOMM_INC(int COMM_INC) {
        this.COMM_INC = COMM_INC;
    }

    public String getCOMM_RATE() {
        return COMM_RATE;
    }

    public void setCOMM_RATE(String COMM_RATE) {
        this.COMM_RATE = COMM_RATE;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public Date getCREATED_DATE() {
        return CREATED_DATE;
    }

    public void setCREATED_DATE(Date CREATED_DATE) {
        this.CREATED_DATE = CREATED_DATE;
    }

    public String getDEVICEID() {
        return DEVICEID;
    }

    public void setDEVICEID(String DEVICEID) {
        this.DEVICEID = DEVICEID;
    }

    public double getDISCOUNT() {
        return DISCOUNT;
    }

    public void setDISCOUNT(double DISCOUNT) {
        this.DISCOUNT = DISCOUNT;
    }

    public double getGRANTTOTAL() {
        return GRANTTOTAL;
    }

    public void setGRANTTOTAL(double GRANTTOTAL) {
        this.GRANTTOTAL = GRANTTOTAL;
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

    public String getPLATFORM() {
        return PLATFORM;
    }

    public void setPLATFORM(String PLATFORM) {
        this.PLATFORM = PLATFORM;
    }

    public int getSAVEFLAG() {
        return SAVEFLAG;
    }

    public void setSAVEFLAG(int SAVEFLAG) {
        this.SAVEFLAG = SAVEFLAG;
    }

    public String getSERVICE_CODE() {
        return SERVICE_CODE;
    }

    public void setSERVICE_CODE(String SERVICE_CODE) {
        this.SERVICE_CODE = SERVICE_CODE;
    }

    public String getSESSIONID() {
        return SESSIONID;
    }

    public void setSESSIONID(String SESSIONID) {
        this.SESSIONID = SESSIONID;
    }

    public String getTRANSACTION_ID() {
        return TRANSACTION_ID;
    }

    public void setTRANSACTION_ID(String TRANSACTION_ID) {
        this.TRANSACTION_ID = TRANSACTION_ID;
    }

    public String getTXID() {
        return TXID;
    }

    public void setTXID(String TXID) {
        this.TXID = TXID;
    }

    public String getUPDATE_BY() {
        return UPDATE_BY;
    }

    public void setUPDATE_BY(String UPDATE_BY) {
        this.UPDATE_BY = UPDATE_BY;
    }

    public Date getUPDATR_DATE() {
        return UPDATR_DATE;
    }

    public void setUPDATR_DATE(Date UPDATR_DATE) {
        this.UPDATR_DATE = UPDATR_DATE;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public int getVAT_INC() {
        return VAT_INC;
    }

    public void setVAT_INC(int VAT_INC) {
        this.VAT_INC = VAT_INC;
    }

    public double getWALLET_AFTER() {
        return WALLET_AFTER;
    }

    public void setWALLET_AFTER(double WALLET_AFTER) {
        this.WALLET_AFTER = WALLET_AFTER;
    }

    public double getWALLET_BEFORE() {
        return WALLET_BEFORE;
    }

    public void setWALLET_BEFORE(double WALLET_BEFORE) {
        this.WALLET_BEFORE = WALLET_BEFORE;
    }
}
