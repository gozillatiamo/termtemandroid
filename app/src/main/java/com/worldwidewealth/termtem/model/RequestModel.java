package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by MyNet on 15/11/2559.
 */

public class RequestModel implements Parcelable{
    private String action;
    private DataRequestModel data;

    public RequestModel(String action, DataRequestModel data){
        this.action = action;
        this.data = data;
    }

    protected RequestModel(Parcel in) {
        this.action = in.readString();
        this.data = in.readParcelable(DataRequestModel.class.getClassLoader());
        Log.e("readTranId", ((SubmitTopupRequestModel)data).getTRANID());

    }

    public static final Creator<RequestModel> CREATOR = new Creator<RequestModel>() {
        @Override
        public RequestModel createFromParcel(Parcel in) {
            return new RequestModel(in);
        }

        @Override
        public RequestModel[] newArray(int size) {
            return new RequestModel[size];
        }
    };

    public String getAction() {
        return action;
    }

    public DataRequestModel getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        Log.e("writeTranId", ((SubmitTopupRequestModel)data).getTRANID());
        dest.writeParcelable(this.data, flags);
    }
}
