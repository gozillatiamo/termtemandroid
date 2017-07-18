package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gozillatiamo on 7/18/17.
 */

public class LoadBillRefResponse implements Parcelable {
    private String BillRef_ID;
    private int No;
    private String LABEL_NAME;
    private int LENGTHCODE;

    public String getBillRef_ID() {
        return BillRef_ID;
    }

    public int getNo() {
        return No;
    }

    public String getLABEL_NAME() {
        return LABEL_NAME;
    }

    public int getLENGTHCODE() {
        return LENGTHCODE;
    }

    public static Creator<LoadBillRefResponse> getCREATOR() {
        return CREATOR;
    }

    protected LoadBillRefResponse(Parcel in) {
        BillRef_ID = in.readString();
        No = in.readInt();
        LABEL_NAME = in.readString();
        LENGTHCODE = in.readInt();
    }

    public static final Creator<LoadBillRefResponse> CREATOR = new Creator<LoadBillRefResponse>() {
        @Override
        public LoadBillRefResponse createFromParcel(Parcel in) {
            return new LoadBillRefResponse(in);
        }

        @Override
        public LoadBillRefResponse[] newArray(int size) {
            return new LoadBillRefResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BillRef_ID);
        parcel.writeInt(No);
        parcel.writeString(LABEL_NAME);
        parcel.writeInt(LENGTHCODE);
    }
}
