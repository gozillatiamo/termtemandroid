package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by user on 09-May-17.
 */

public class AttachResponseModel implements Parcelable {
    private String TXID;
    private String MSGID;
    private String URLFILE;
    private String FILETYPE;
    private String CONTENTTYPE;
    private Double FILESIZE;
    private Date CREATED_DATE;
    private String CREATED_BY;
    private Date UPDATR_DATE;
    private String UPDATE_BY;
    private int ACTIVE;
    private int SORTNO;

    public AttachResponseModel() {
    }

    protected AttachResponseModel(Parcel in) {
        TXID = in.readString();
        MSGID = in.readString();
        URLFILE = in.readString();
        FILETYPE = in.readString();
        CONTENTTYPE = in.readString();
        CREATED_BY = in.readString();
        UPDATE_BY = in.readString();
        ACTIVE = in.readInt();
        SORTNO = in.readInt();
        FILESIZE = in.readDouble();
        CREATED_DATE = new Date(in.readLong());
        UPDATR_DATE = new Date(in.readLong());
    }

    public static final Creator<AttachResponseModel> CREATOR = new Creator<AttachResponseModel>() {
        @Override
        public AttachResponseModel createFromParcel(Parcel in) {
            return new AttachResponseModel(in);
        }

        @Override
        public AttachResponseModel[] newArray(int size) {
            return new AttachResponseModel[size];
        }
    };

    public String getTXID() {
        return TXID;
    }

    public String getMSGID() {
        return MSGID;
    }

    public String getURLFILE() {
        return URLFILE;
    }

    public String getFILETYPE() {
        return FILETYPE;
    }

    public String getCONTENTTYPE() {
        return CONTENTTYPE;
    }

    public Double getFILESIZE() {
        return FILESIZE;
    }

    public Date getCREATED_DATE() {
        return CREATED_DATE;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public Date getUPDATR_DATE() {
        return UPDATR_DATE;
    }

    public String getUPDATE_BY() {
        return UPDATE_BY;
    }

    public int getACTIVE() {
        return ACTIVE;
    }

    public int getSORTNO() {
        return SORTNO;
    }

    public void setTXID(String TXID) {
        this.TXID = TXID;
    }

    public void setMSGID(String MSGID) {
        this.MSGID = MSGID;
    }

    public void setURLFILE(String URLFILE) {
        this.URLFILE = URLFILE;
    }

    public void setFILETYPE(String FILETYPE) {
        this.FILETYPE = FILETYPE;
    }

    public void setCONTENTTYPE(String CONTENTTYPE) {
        this.CONTENTTYPE = CONTENTTYPE;
    }

    public void setFILESIZE(Double FILESIZE) {
        this.FILESIZE = FILESIZE;
    }

    public void setCREATED_DATE(Date CREATED_DATE) {
        this.CREATED_DATE = CREATED_DATE;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public void setUPDATR_DATE(Date UPDATR_DATE) {
        this.UPDATR_DATE = UPDATR_DATE;
    }

    public void setUPDATE_BY(String UPDATE_BY) {
        this.UPDATE_BY = UPDATE_BY;
    }

    public void setACTIVE(int ACTIVE) {
        this.ACTIVE = ACTIVE;
    }

    public void setSORTNO(int SORTNO) {
        this.SORTNO = SORTNO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(TXID);
        parcel.writeString(MSGID);
        parcel.writeString(URLFILE);
        parcel.writeString(FILETYPE);
        parcel.writeString(CONTENTTYPE);
        parcel.writeString(CREATED_BY);
        parcel.writeString(UPDATE_BY);
        parcel.writeInt(ACTIVE);
        parcel.writeInt(SORTNO);
        parcel.writeDouble(FILESIZE);
        parcel.writeLong(CREATED_DATE.getTime());
        parcel.writeLong(UPDATR_DATE.getTime());
    }


}
