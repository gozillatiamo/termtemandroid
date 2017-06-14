package com.worldwidewealth.termtem.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;

/**
 * Created by MyNet on 22/11/2559.
 */

public class SubmitTopupRequestModel extends DataRequestModel implements Parcelable{
    private String CARRIER;
    private String AMT;
    private String PHONENO;
    private String TRANID;
    private String AGENTIDCASHIN;
    private String PGID;
    private String PGNAME;
    private String BUTTONID;

    public static SubmitTopupRequestModel SubmitAgentRequestModel(String amt, String phone_no, String tran_id, String agent_id){
       return new SubmitTopupRequestModel(amt, null, phone_no, tran_id, null, agent_id, null, null);
    }

    public static SubmitTopupRequestModel SubmitTopupRequestModel(String amt, String carrier, String phone_no, String tran_id, String button_id){
        return new SubmitTopupRequestModel(amt, carrier, phone_no, tran_id, button_id, null, null, null);
    }

    public static SubmitTopupRequestModel SubmitVasRequestModel(String amt, String carrier, String phone_no, String tran_id, String pg_name, String pg_id){
        return new SubmitTopupRequestModel(amt, carrier, phone_no, tran_id, null, null, pg_id, pg_name);
    }

    public SubmitTopupRequestModel(String amt, String carrier, String phone_no, String tran_id,
                                   String button_id, String agent_id, String pg_id, String pg_name) {
        this.AMT = EncryptionData.EncryptData(amt, Global.getInstance().getDEVICEID()+tran_id);
        this.CARRIER = EncryptionData.EncryptData(carrier, Global.getInstance().getDEVICEID() + tran_id);
        this.PHONENO = EncryptionData.EncryptData(phone_no, Global.getInstance().getDEVICEID()+tran_id);
        this.TRANID = tran_id;
        this.PGNAME = pg_name;
        this.BUTTONID = button_id;
        this.AGENTIDCASHIN = agent_id;
        this.PGID = pg_id;
    }

    protected SubmitTopupRequestModel(Parcel in) {
        this.CARRIER = in.readString();
        this.AMT = in.readString();
        this.PHONENO = in.readString();
        this.TRANID = in.readString();
        this.AGENTIDCASHIN = in.readString();
        this.BUTTONID = in.readString();
        this.PGNAME = in.readString();
        this.PGID = in.readString();
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

    public String getPGID() {
        return PGID;
    }

    public String getPGNAME() {
        return PGNAME;
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
        dest.writeString(this.CARRIER);
        dest.writeString(this.AMT);
        dest.writeString(this.PHONENO);
        dest.writeString(this.TRANID);
        dest.writeString(this.AGENTIDCASHIN);
        dest.writeString(this.BUTTONID);
        dest.writeString(this.PGID);
        dest.writeString(this.PGNAME);
    }
}
