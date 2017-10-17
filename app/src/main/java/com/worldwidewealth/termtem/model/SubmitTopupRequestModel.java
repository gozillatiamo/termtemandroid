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

    private String TNID;
    private String BILL_SERVICE_ID;
    private String BILL_SERVICE_CODE;
    private String USEBARCODE;
    private String DUEDATE;


    protected SubmitTopupRequestModel(Parcel in) {
        super(in);
        CARRIER = in.readString();
        AMT = in.readString();
        PHONENO = in.readString();
        TRANID = in.readString();
        AGENTIDCASHIN = in.readString();
        PGID = in.readString();
        PGNAME = in.readString();
        BUTTONID = in.readString();
        TNID = in.readString();
        BILL_SERVICE_ID = in.readString();
        BILL_SERVICE_CODE = in.readString();
        USEBARCODE = in.readString();
        DUEDATE = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(CARRIER);
        dest.writeString(AMT);
        dest.writeString(PHONENO);
        dest.writeString(TRANID);
        dest.writeString(AGENTIDCASHIN);
        dest.writeString(PGID);
        dest.writeString(PGNAME);
        dest.writeString(BUTTONID);
        dest.writeString(TNID);
        dest.writeString(BILL_SERVICE_ID);
        dest.writeString(BILL_SERVICE_CODE);
        dest.writeString(USEBARCODE);
        dest.writeString(DUEDATE);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public static SubmitTopupRequestModel SubmitAgentRequestModel(String amt, String phone_no, String tran_id, String agent_id){
       return new SubmitTopupRequestModel(amt, null, phone_no, tran_id, null, agent_id, null, null);
    }

    public static SubmitTopupRequestModel SubmitTopupRequestModel(String amt, String carrier, String phone_no, String tran_id, String button_id){
        return new SubmitTopupRequestModel(amt, carrier, phone_no, tran_id, button_id, null, null, null);
    }

    public static SubmitTopupRequestModel SubmitVasRequestModel(String amt, String carrier, String phone_no, String tran_id, String pg_name, String pg_id){
        return new SubmitTopupRequestModel(amt, carrier, phone_no, tran_id, null, null, pg_id, pg_name);
    }

    public static SubmitTopupRequestModel SubmitBillRequestModel(String tnid, String bill_service_id, String bill_service_code,
                                                                 String userbarcode, String duedate, String phoneno, String tranid){
        return new SubmitTopupRequestModel(tnid, bill_service_id, bill_service_code, userbarcode,
                duedate, phoneno, tranid);
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

    public SubmitTopupRequestModel(String tnid, String bill_service_id, String bill_service_code,
                                   String userbarcode, String duedate, String phoneno, String tranid){
        this.TNID = tnid;
        this.BILL_SERVICE_ID = bill_service_id;
        this.BILL_SERVICE_CODE = bill_service_code;
        this.USEBARCODE = userbarcode;
        this.DUEDATE = duedate;
        this.PHONENO = phoneno;
        this.TRANID = tranid;

    }

    public String getCARRIER() {
        if (CARRIER != null)
            return EncryptionData.DecryptData(CARRIER, Global.getInstance().getDEVICEID() + TRANID);

        return CARRIER;
    }

    public String getAMT() {
        String encode;
        if (AMT != null) {
            encode = EncryptionData.DecryptData(AMT, Global.getInstance().getDEVICEID() + TRANID);
            encode = (encode == null || encode.isEmpty()) ? AMT : encode;
            return encode;
        }

        return "0";
    }

    public String getPHONENO() {
        String encode;
        if (PHONENO != null) {
            encode =  EncryptionData.DecryptData(PHONENO, Global.getInstance().getDEVICEID() + TRANID);
            encode = (encode == null || encode.isEmpty()) ? PHONENO : encode;
            return encode;

        }

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

    public String getTNID() {
        return TNID;
    }

    public String getBILL_SERVICE_ID() {
        return BILL_SERVICE_ID;
    }

    public String getBILL_SERVICE_CODE() {
        return BILL_SERVICE_CODE;
    }

    public String getUSEBARCODE() {
        return USEBARCODE;
    }

    public String getDUEDATE() {
        return DUEDATE;
    }

    public void setAMT(String AMT) {
        this.AMT = AMT;
    }
}
