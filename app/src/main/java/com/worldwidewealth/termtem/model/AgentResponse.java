package com.worldwidewealth.termtem.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Created by user on 14-Feb-17.
 */

public class AgentResponse implements Parcelable {

    private String AgentId;
    private String AgentCode;
    private String FirstName;
    private String LastName;
    private String Phoneno;
    private String TXID;
    private boolean IsTermTemScan = true;

    public AgentResponse(String agentid, String agentcode, String firstname, String lastname, String phoneno) {
        new AgentResponse(agentid, agentcode, firstname, lastname, phoneno, null);
    }

    public AgentResponse(String agentid, String agentcode, String firstname, String lastname, String phoneno, String txid) {
        this.AgentId = agentid;
        this.AgentCode = agentcode;
        this.FirstName = firstname;
        this.LastName = lastname;
        this.Phoneno = phoneno;
        this.TXID = txid;
    }


    protected AgentResponse(Parcel in) {
        AgentId = in.readString();
        AgentCode = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        Phoneno = in.readString();
        TXID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(AgentId);
        dest.writeString(AgentCode);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(Phoneno);
        dest.writeString(TXID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AgentResponse> CREATOR = new Creator<AgentResponse>() {
        @Override
        public AgentResponse createFromParcel(Parcel in) {
            return new AgentResponse(in);
        }

        @Override
        public AgentResponse[] newArray(int size) {
            return new AgentResponse[size];
        }
    };

    public String getAgentId() {
        return AgentId;
    }

    public void setAgentId(String agentId) {
        AgentId = agentId;
    }

    public String getTXID() {
        return TXID;
    }

    public String getAgentCode() {
        return AgentCode;
    }

    public static Creator<AgentResponse> getCREATOR() {
        return CREATOR;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getPhoneno() {
        return Phoneno;
    }
}
