package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MyNet on 16/11/2559.
 */

public class TopupPreviewResponseModel implements Parcelable{
    private double AMOUNT;
    private String COMMISSION_RATE;
    private double COMMISSION_AMOUNT;
    private double BALANCE;
    private double TOTAL;
    private double MARKUP;
    private String TNID;
    private double FEE;
    private double NET;
    private String REF1;
    private String NEEDDUEDATE;

    protected TopupPreviewResponseModel(Parcel in) {
        AMOUNT = in.readDouble();
        COMMISSION_RATE = in.readString();
        COMMISSION_AMOUNT = in.readDouble();
        BALANCE = in.readDouble();
        TOTAL = in.readDouble();
        MARKUP = in.readDouble();
        TNID = in.readString();
        FEE = in.readDouble();
        NET = in.readDouble();
        REF1 = in.readString();
        NEEDDUEDATE = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(AMOUNT);
        dest.writeString(COMMISSION_RATE);
        dest.writeDouble(COMMISSION_AMOUNT);
        dest.writeDouble(BALANCE);
        dest.writeDouble(TOTAL);
        dest.writeDouble(MARKUP);
        dest.writeString(TNID);
        dest.writeDouble(FEE);
        dest.writeDouble(NET);
        dest.writeString(REF1);
        dest.writeString(NEEDDUEDATE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TopupPreviewResponseModel> CREATOR = new Creator<TopupPreviewResponseModel>() {
        @Override
        public TopupPreviewResponseModel createFromParcel(Parcel in) {
            return new TopupPreviewResponseModel(in);
        }

        @Override
        public TopupPreviewResponseModel[] newArray(int size) {
            return new TopupPreviewResponseModel[size];
        }
    };

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

    public double getMARKUP() {
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

    public String getNEEDDUEDATE() {
        return NEEDDUEDATE;
    }
}
