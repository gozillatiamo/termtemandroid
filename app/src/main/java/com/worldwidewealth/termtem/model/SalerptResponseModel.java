package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by MyNet on 24/11/2559.
 */

public class SalerptResponseModel  implements Parcelable{
    private String PAYCODE;
    private double AMOUNT;
    private double COMM_AMT;
    private double CHECKTOTAL;
    private Date PAYMENT_DATE;
    private String BILLER;
    private String TYPE;
    private String PHONENO;
    private double MARKUP_AMT;
    private String AGENTNAME;
    private String AGENTCASHINID;


    protected SalerptResponseModel(Parcel in) {
        PAYCODE = in.readString();
        AMOUNT = in.readDouble();
        COMM_AMT = in.readDouble();
        CHECKTOTAL = in.readDouble();
        BILLER = in.readString();
        TYPE = in.readString();
        PHONENO = in.readString();
        MARKUP_AMT = in.readDouble();
        AGENTNAME = in.readString();
        AGENTCASHINID = in.readString();
    }

    public static final Creator<SalerptResponseModel> CREATOR = new Creator<SalerptResponseModel>() {
        @Override
        public SalerptResponseModel createFromParcel(Parcel in) {
            return new SalerptResponseModel(in);
        }

        @Override
        public SalerptResponseModel[] newArray(int size) {
            return new SalerptResponseModel[size];
        }
    };

    public String getAGENTCASHINID() {
        return AGENTCASHINID;
    }

    public String getAGENTNAME() {
        return AGENTNAME;
    }

    public double getMARKUP_AMT() {
        return MARKUP_AMT;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PAYCODE);
        dest.writeDouble(AMOUNT);
        dest.writeDouble(COMM_AMT);
        dest.writeDouble(CHECKTOTAL);
        dest.writeString(BILLER);
        dest.writeString(TYPE);
        dest.writeString(PHONENO);
        dest.writeDouble(MARKUP_AMT);
        dest.writeString(AGENTNAME);
        dest.writeString(AGENTCASHINID);
    }
}
