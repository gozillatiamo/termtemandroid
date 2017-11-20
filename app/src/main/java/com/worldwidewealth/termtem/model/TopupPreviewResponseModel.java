package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private RefModel[] REF;
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
        REF = (RefModel[]) in.readParcelableArray(RefModel.class.getClassLoader());
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
        dest.writeParcelableArray(REF, flags);
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

    public String getNEEDDUEDATE() {
        return NEEDDUEDATE;
    }

    public List<RefModel> getREF() {
        if (REF == null) return null;

        List<RefModel> list = Arrays.asList(REF);
        return list;
    }

    public static class RefModel implements Parcelable, Comparable<RefModel>{
        private int REF_NO;
        private String REF_NAME;
        private String REF_VALUE;

        protected RefModel(Parcel in) {
            REF_NO = in.readInt();
            REF_NAME = in.readString();
            REF_VALUE = in.readString();
        }

        public static final Creator<RefModel> CREATOR = new Creator<RefModel>() {
            @Override
            public RefModel createFromParcel(Parcel in) {
                return new RefModel(in);
            }

            @Override
            public RefModel[] newArray(int size) {
                return new RefModel[size];
            }
        };

        public int getREF_NO() {
            return REF_NO;
        }

        public void setREF_NO(int REF_NO) {
            this.REF_NO = REF_NO;
        }

        public String getREF_NAME() {
            return REF_NAME;
        }

        public void setREF_NAME(String REF_NAME) {
            this.REF_NAME = REF_NAME;
        }

        public String getREF_VALUE() {
            return REF_VALUE;
        }

        public void setREF_VALUE(String REF_VALUE) {
            this.REF_VALUE = REF_VALUE;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(REF_NO);
            parcel.writeString(REF_NAME);
            parcel.writeString(REF_VALUE);
        }

        @Override
        public int compareTo(@NonNull RefModel model) {
            if (REF_NO > model.getREF_NO())
                return 1;
            if (REF_NO < model.getREF_NO())
                return -1;
            else
                return 0;

        }
    }



}
