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
    private double CALFEE;
    private Date PAYMENT_DATE;
    private String BILLNAME;
    private String BILLER;
    private String REF1;
    private String TYPE;
    private String PHONENO;
    private double MARKUP_AMT;
    private String AGENTNAME;
    private String AGENTCASHINID;
    private String TransactionId;

    protected SalerptResponseModel(Parcel in) {
        PAYCODE = in.readString();
        AMOUNT = in.readDouble();
        COMM_AMT = in.readDouble();
        CHECKTOTAL = in.readDouble();
        CALFEE = in.readDouble();
        BILLNAME = in.readString();
        BILLER = in.readString();
        REF1 = in.readString();
        TYPE = in.readString();
        PHONENO = in.readString();
        MARKUP_AMT = in.readDouble();
        AGENTNAME = in.readString();
        AGENTCASHINID = in.readString();
        TransactionId = in.readString();
        PAYMENT_DATE = new Date(in.readLong());

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PAYCODE);
        dest.writeDouble(AMOUNT);
        dest.writeDouble(COMM_AMT);
        dest.writeDouble(CHECKTOTAL);
        dest.writeDouble(CALFEE);
        dest.writeString(BILLNAME);
        dest.writeString(BILLER);
        dest.writeString(REF1);
        dest.writeString(TYPE);
        dest.writeString(PHONENO);
        dest.writeDouble(MARKUP_AMT);
        dest.writeString(AGENTNAME);
        dest.writeString(AGENTCASHINID);
        dest.writeString(TransactionId);
        dest.writeLong(PAYMENT_DATE.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
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


    public String getPHONENO() {
        return PHONENO;
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


    public double getCHECKTOTAL() {
        return CHECKTOTAL;
    }


    public double getCOMM_AMT() {
        return COMM_AMT;
    }


    public String getPAYCODE() {
        return PAYCODE;
    }


    public Date getPAYMENT_DATE() {
        return PAYMENT_DATE;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public double getCALFEE() {
        return CALFEE;
    }

    public String getBILLNAME() {
        return BILLNAME;
    }

    public String getREF1() {
        return REF1;
    }
}
