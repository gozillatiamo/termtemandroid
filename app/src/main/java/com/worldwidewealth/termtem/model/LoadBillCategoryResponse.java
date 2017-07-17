package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 17-Jul-17.
 */

public class LoadBillCategoryResponse implements Parcelable {

    private String BILLPAY_CATEGORY_ID;
    private String BILLPAY_CATEGORY_NAME;
    private int SORTNO;
    private String LOGOURL;

    protected LoadBillCategoryResponse(Parcel in) {
        BILLPAY_CATEGORY_ID = in.readString();
        BILLPAY_CATEGORY_NAME = in.readString();
        SORTNO = in.readInt();
        LOGOURL = in.readString();
    }

    public static final Creator<LoadBillCategoryResponse> CREATOR = new Creator<LoadBillCategoryResponse>() {
        @Override
        public LoadBillCategoryResponse createFromParcel(Parcel in) {
            return new LoadBillCategoryResponse(in);
        }

        @Override
        public LoadBillCategoryResponse[] newArray(int size) {
            return new LoadBillCategoryResponse[size];
        }
    };

    public String getBILLPAY_CATEGORY_ID() {
        return BILLPAY_CATEGORY_ID;
    }

    public String getBILLPAY_CATEGORY_NAME() {
        return BILLPAY_CATEGORY_NAME;
    }

    public int getSORTNO() {
        return SORTNO;
    }

    public String getLOGOURL() {
        return LOGOURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BILLPAY_CATEGORY_ID);
        parcel.writeString(BILLPAY_CATEGORY_NAME);
        parcel.writeInt(SORTNO);
        parcel.writeString(LOGOURL);
    }
}
