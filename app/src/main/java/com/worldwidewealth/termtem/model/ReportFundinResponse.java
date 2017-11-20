package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by gozillatiamo on 10/31/17.
 */

public class ReportFundinResponse implements Parcelable{
    //Payment Type
    public static final String BANK = "01";
    public static final String AGENT = "02";
    public static final String REFUND = "03";
    public static final String MPAY = "04";
    public static final String BONUS = "05";

    private double CREDIT;
    private String PAYMENT_TYPE;
    private Date CREATED_DATE;
    private String Bank;

    protected ReportFundinResponse(Parcel in) {
        CREDIT = in.readDouble();
        PAYMENT_TYPE = in.readString();
        Bank = in.readString();
        CREATED_DATE = new Date(in.readLong());

    }

    public static final Creator<ReportFundinResponse> CREATOR = new Creator<ReportFundinResponse>() {
        @Override
        public ReportFundinResponse createFromParcel(Parcel in) {
            return new ReportFundinResponse(in);
        }

        @Override
        public ReportFundinResponse[] newArray(int size) {
            return new ReportFundinResponse[size];
        }
    };

    public double getCREDIT() {
        return CREDIT;
    }

    public void setCREDIT(double CREDIT) {
        this.CREDIT = CREDIT;
    }

    public String getPAYMENT_TYPE() {
        return PAYMENT_TYPE;
    }

    public void setPAYMENT_TYPE(String PAYMENT_TYPE) {
        this.PAYMENT_TYPE = PAYMENT_TYPE;
    }

    public Date getCREATED_DATE() {
        return CREATED_DATE;
    }

    public void setCREATED_DATE(Date CREATED_DATE) {
        this.CREATED_DATE = CREATED_DATE;
    }

    public String getBank() {
        return Bank;
    }

    public void setBank(String bank) {
        Bank = bank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(CREDIT);
        parcel.writeString(PAYMENT_TYPE);
        parcel.writeString(Bank);
        parcel.writeLong(CREATED_DATE.getTime());

    }
}
