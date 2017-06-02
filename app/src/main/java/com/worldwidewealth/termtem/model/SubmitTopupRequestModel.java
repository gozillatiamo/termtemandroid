package com.worldwidewealth.termtem.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;

/**
 * Created by MyNet on 22/11/2559.
 */

public class SubmitTopupRequestModel extends DataRequestModel implements Parcelable{
    private String CARRIER;
    private String AMT;
    private String PHONENO;
    private String TRANID;
    private String AGENTIDCASHIN;
//    private String OTP;
    private String BUTTONID;

    public SubmitTopupRequestModel(String AMT, String CARRIER, String PHONENO, String TRANID, String BUTTONID, String AGENTIDCASHIN) {
        this.AMT = EncryptionData.EncryptData(AMT, Global.getInstance().getDEVICEID()+TRANID);

        if (CARRIER != null) {
            this.CARRIER = EncryptionData.EncryptData(CARRIER, Global.getInstance().getDEVICEID() + TRANID);
        } else {
            this.CARRIER = CARRIER;
        }

        this.AGENTIDCASHIN = AGENTIDCASHIN;

        this.PHONENO = EncryptionData.EncryptData(PHONENO, Global.getInstance().getDEVICEID()+TRANID);

        this.TRANID = TRANID;

        this.BUTTONID = BUTTONID;
    }

    protected SubmitTopupRequestModel(Parcel in) {
        CARRIER = in.readString();
        AMT = in.readString();
        PHONENO = in.readString();
        TRANID = in.readString();
        AGENTIDCASHIN = in.readString();
        BUTTONID = in.readString();
    }

    public String getCARRIER() {
        if (CARRIER != null)
            return EncryptionData.DecryptData(CARRIER, Global.getInstance().getDEVICEID() + TRANID);

        return CARRIER;
    }

    public String getAMT() {
        if (AMT != null)
            return EncryptionData.DecryptData(AMT, Global.getInstance().getDEVICEID() + TRANID);

        return AMT;
    }

    public String getPHONENO() {
        if (PHONENO != null)
            return EncryptionData.DecryptData(PHONENO, Global.getInstance().getDEVICEID() + TRANID);

        return PHONENO;
    }

    public String getTRANID() {
        return TRANID;
    }

    public String getAGENTIDCASHIN() {
        return AGENTIDCASHIN;
    }

    public String getBUTTONID() {
        return BUTTONID;
    }

    public static final Creator<SubmitTopupRequestModel> CREATOR = new Creator<SubmitTopupRequestModel>() {
        @Override
        public SubmitTopupRequestModel createFromParcel(Parcel in) {
            return new SubmitTopupRequestModel(in);
        }

        @Override
        public SubmitTopupRequestModel[] newArray(int size) {
            return new SubmitTopupRequestModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CARRIER);
        dest.writeString(AMT);
        dest.writeString(PHONENO);
        dest.writeString(TRANID);
        dest.writeString(AGENTIDCASHIN);
        dest.writeString(BUTTONID);
    }
}
