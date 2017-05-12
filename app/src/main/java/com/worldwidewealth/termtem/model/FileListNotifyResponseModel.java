package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 12-May-17.
 */

public class FileListNotifyResponseModel implements Parcelable{
    private String contentype;
    private String filetype;
    private Double size;
    private String url;

    protected FileListNotifyResponseModel(Parcel in) {
        contentype = in.readString();
        filetype = in.readString();
        size = in.readDouble();
        url = in.readString();
    }

    public static final Creator<FileListNotifyResponseModel> CREATOR = new Creator<FileListNotifyResponseModel>() {
        @Override
        public FileListNotifyResponseModel createFromParcel(Parcel in) {
            return new FileListNotifyResponseModel(in);
        }

        @Override
        public FileListNotifyResponseModel[] newArray(int size) {
            return new FileListNotifyResponseModel[size];
        }
    };

    public String getContentype() {
        return contentype;
    }

    public String getFiletype() {
        return filetype;
    }

    public Double getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contentype);
        parcel.writeString(filetype);
        parcel.writeDouble(size);
        parcel.writeString(url);
    }
}
