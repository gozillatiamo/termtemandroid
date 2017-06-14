package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gozillatiamo on 6/14/17.
 */

public class PGResponseModel implements Parcelable{
    private String serviceNameTh;
    private String packageId;
    private double amount;
    private int SortNo;

    protected PGResponseModel(Parcel in) {
        serviceNameTh = in.readString();
        packageId = in.readString();
        amount = in.readDouble();
        SortNo = in.readInt();
    }

    public static final Creator<PGResponseModel> CREATOR = new Creator<PGResponseModel>() {
        @Override
        public PGResponseModel createFromParcel(Parcel in) {
            return new PGResponseModel(in);
        }

        @Override
        public PGResponseModel[] newArray(int size) {
            return new PGResponseModel[size];
        }
    };

    public String getServiceNameTh() {
        return serviceNameTh;
    }

    public String getPackageId() {
        return packageId;
    }

    public double getAmount() {
        return amount;
    }

    public int getSortNo() {
        return SortNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serviceNameTh);
        dest.writeString(packageId);
        dest.writeDouble(amount);
        dest.writeInt(SortNo);
    }
}
