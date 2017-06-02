package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by gozillatiamo on 6/2/17.
 */

public class ChartResponseModel implements Parcelable {

    private double AMOUNT;
    private Date PAYMENT_DATE;

    protected ChartResponseModel(Parcel in) {
        AMOUNT = in.readDouble();
        PAYMENT_DATE.setTime(in.readLong());
    }

    public double getAMOUNT() {
        return AMOUNT;
    }

    public Date getPAYMENT_DATE() {
        return PAYMENT_DATE;
    }

    public static final Creator<ChartResponseModel> CREATOR = new Creator<ChartResponseModel>() {
        @Override
        public ChartResponseModel createFromParcel(Parcel in) {
            return new ChartResponseModel(in);
        }

        @Override
        public ChartResponseModel[] newArray(int size) {
            return new ChartResponseModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(AMOUNT);
        dest.writeLong(PAYMENT_DATE.getTime());
    }
}
