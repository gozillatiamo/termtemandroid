package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by user on 17-Jul-17.
 */

public class LoadBillServiceResponse implements Parcelable, Comparable<LoadBillServiceResponse>{
    private String BILL_SERVICE_ID;
    private String BILL_SERVICE_NAME;
    private String BILL_SERVICE_CODE;
    private String BILL_SERVICE_DESC;
    private String MPAY_SERVICE_NAME;
    private String LOGOURL;
    private int KEYIN;
    private int SCAN;
    private double FEE;
    private String COMMAND;
    private int SORTNO;

    protected LoadBillServiceResponse(Parcel in) {
        BILL_SERVICE_ID = in.readString();
        BILL_SERVICE_NAME = in.readString();
        BILL_SERVICE_CODE = in.readString();
        BILL_SERVICE_DESC = in.readString();
        MPAY_SERVICE_NAME = in.readString();
        LOGOURL = in.readString();
        KEYIN = in.readInt();
        SCAN = in.readInt();
        FEE = in.readDouble();
        COMMAND = in.readString();
        SORTNO = in.readInt();
    }

    public static final Creator<LoadBillServiceResponse> CREATOR = new Creator<LoadBillServiceResponse>() {
        @Override
        public LoadBillServiceResponse createFromParcel(Parcel in) {
            return new LoadBillServiceResponse(in);
        }

        @Override
        public LoadBillServiceResponse[] newArray(int size) {
            return new LoadBillServiceResponse[size];
        }
    };

    public String getBILL_SERVICE_ID() {
        return BILL_SERVICE_ID;
    }

    public String getBILL_SERVICE_NAME() {
        return BILL_SERVICE_NAME;
    }

    public String getBILL_SERVICE_CODE() {
        return BILL_SERVICE_CODE;
    }

    public String getBILL_SERVICE_DESC() {
        return BILL_SERVICE_DESC;
    }

    public String getMPAY_SERVICE_NAME() {
        return MPAY_SERVICE_NAME;
    }

    public String getLOGOURL() {
        return LOGOURL;
    }

    public int getKEYIN() {
        return KEYIN;
    }

    public int getSCAN() {
        return SCAN;
    }

    public double getFEE() {
        return FEE;
    }

    public String getCOMMAND() {
        return COMMAND;
    }

    public int getSORTNO() {
        return SORTNO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BILL_SERVICE_ID);
        parcel.writeString(BILL_SERVICE_NAME);
        parcel.writeString(BILL_SERVICE_CODE);
        parcel.writeString(BILL_SERVICE_DESC);
        parcel.writeString(MPAY_SERVICE_NAME);
        parcel.writeString(LOGOURL);
        parcel.writeInt(KEYIN);
        parcel.writeInt(SCAN);
        parcel.writeDouble(FEE);
        parcel.writeString(COMMAND);
        parcel.writeInt(SORTNO);
    }

    @Override
    public int compareTo(@NonNull LoadBillServiceResponse billServiceResponse) {
        if (SORTNO > billServiceResponse.getSORTNO())
            return 1;
        if (SORTNO < billServiceResponse.getSORTNO())
            return -1;
        else
            return 0;
    }
}
