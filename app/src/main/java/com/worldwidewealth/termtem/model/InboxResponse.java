package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by user on 14-Feb-17.
 */

public class InboxResponse implements Parcelable {
    private String msgid;
    private String title;
    private String msg;
    private boolean Readed;
    private Date Create_Date;


    public String getMsgid() {
        return msgid;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isReaded() {
        return Readed;
    }

    public void setReaded(boolean readed) {
        Readed = readed;
    }

    public Date getCreate_Date() {
        return Create_Date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected InboxResponse(Parcel in){
        msgid = in.readString();
        title = in.readString();
        msg = in.readString();
        Readed = in.readByte() != 0;
        Create_Date = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgid);
        dest.writeString(title);
        dest.writeString(msg);
        dest.writeByte((byte) (Readed ? 1:0));
        dest.writeLong(Create_Date.getTime());
    }

    public static final Creator<InboxResponse> CREATOR = new Creator<InboxResponse>() {
        @Override
        public InboxResponse createFromParcel(Parcel source) {
            return new InboxResponse(source);
        }

        @Override
        public InboxResponse[] newArray(int size) {
            return new InboxResponse[size];
        }
    };

    public static Creator<InboxResponse> getCreator(){
        return CREATOR;
    }
}
