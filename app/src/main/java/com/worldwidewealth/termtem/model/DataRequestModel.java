package com.worldwidewealth.termtem.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DataRequestModel implements Parcelable{

    private String DEVICEID = Global.getInstance().getDEVICEID();
    private String PLATFORM = MyApplication.getContext().getString(R.string.platform);
    private String TXID = Global.getInstance().getTXID();
    private String AGENTID = EncryptionData.EncryptData(
            EncryptionData.DecryptData(Global.getInstance().getAGENTID(), Global.getInstance().getTXID()),
            Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
    private String USERID = EncryptionData.EncryptData(
            EncryptionData.DecryptData(Global.getInstance().getUSERID(), Global.getInstance().getTXID()),
            Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());

    public DataRequestModel() {
    }

    public DataRequestModel(String agentid, String userid) {
        AGENTID = EncryptionData.EncryptData(
                EncryptionData.DecryptData(agentid, Global.getInstance().getTXID()),
                Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
        USERID = EncryptionData.EncryptData(
                EncryptionData.DecryptData(userid, Global.getInstance().getTXID()),
                Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());

    }

    public DataRequestModel(String deviceid, String platform, String txid, String agentid, String userid) {
        DEVICEID = deviceid;
        PLATFORM = platform;
        TXID = txid;
        AGENTID = EncryptionData.EncryptData(
                EncryptionData.DecryptData(agentid, TXID),
                DEVICEID+TXID);
        USERID = EncryptionData.EncryptData(
                EncryptionData.DecryptData(userid, TXID),
                DEVICEID+TXID);

    }


    protected DataRequestModel(Parcel in) {
        DEVICEID = in.readString();
        PLATFORM = in.readString();
        TXID = in.readString();
        AGENTID = in.readString();
        USERID = in.readString();
    }

    public static final Creator<DataRequestModel> CREATOR = new Creator<DataRequestModel>() {
        @Override
        public DataRequestModel createFromParcel(Parcel in) {
            return new DataRequestModel(in);
        }

        @Override
        public DataRequestModel[] newArray(int size) {
            return new DataRequestModel[size];
        }
    };

    public String getDEVICEID() {
        return DEVICEID;
    }

    public String getPLATFORM() {
        return PLATFORM;
    }

    public String getTXID() {
        return TXID;
    }

    public String getAGENTID() {
        return AGENTID;
    }

    public String getUSERID() {
        return USERID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DEVICEID);
        dest.writeString(PLATFORM);
        dest.writeString(TXID);
        dest.writeString(AGENTID);
        dest.writeString(USERID);
    }
}
