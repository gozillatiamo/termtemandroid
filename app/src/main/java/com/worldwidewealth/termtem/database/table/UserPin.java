package com.worldwidewealth.termtem.database.table;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;

/**
 * Created by gozillatiamo on 11/23/17.
 */

@Entity(tableName = "user_pin", indices = @Index("user_id"))
public class UserPin implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    private String userid;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "pin_id")
    private String pinId;

    @ColumnInfo(name = "use_fingerprint")
    private boolean useFingerprint;

    public UserPin(){

    }

    protected UserPin(Parcel in) {
        userid = in.readString();
        password = in.readString();
        pinId = in.readString();
        useFingerprint = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(password);
        dest.writeString(pinId);
        dest.writeByte((byte) (useFingerprint ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserPin> CREATOR = new Creator<UserPin>() {
        @Override
        public UserPin createFromParcel(Parcel in) {
            return new UserPin(in);
        }

        @Override
        public UserPin[] newArray(int size) {
            return new UserPin[size];
        }
    };

    public String getUserid() {
        String userDecoded = EncryptionData.DecryptData(userid, Global.getInstance().getDEVICEID());
        return userDecoded;
    }

    public void setUserid(String userid) {
        String userEncoded = EncryptionData.EncryptData(userid, Global.getInstance().getDEVICEID());
        this.userid = userEncoded;
    }

    public String getPinId() {
        return pinId;
    }

    public void setPinId(String pinId) {
        this.pinId = pinId;
    }

    public String getPassword() {
        String passwordDecoded = EncryptionData.DecryptData(password, Global.getInstance().getDEVICEID());
        return passwordDecoded;
    }

    public void setPassword(String password) {
        String passwordEncoded = EncryptionData.EncryptData(password, Global.getInstance().getDEVICEID());
        this.password = passwordEncoded;
    }

    public boolean isUseFingerprint() {
        return useFingerprint;
    }

    public void setUseFingerprint(boolean useFingerprint) {
        this.useFingerprint = useFingerprint;
    }
}
