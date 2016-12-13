package com.worldwidewealth.wealthwallet.model;

import com.worldwidewealth.wealthwallet.Configs;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.Global;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DataRequestModel {

    private String DEVICEID = Global.getDEVICEID();
    private String PLATFORM = Configs.getPLATFORM();
    private String TXID = Global.getTXID();
    private String AGENTID = EncryptionData.EncryptData(
            EncryptionData.DecryptData(Global.getAGENTID(), Global.getTXID()),
            Global.getDEVICEID()+Global.getTXID());
    private String USERID = EncryptionData.EncryptData(
            EncryptionData.DecryptData(Global.getUSERID(), Global.getTXID()),
            Global.getDEVICEID()+Global.getTXID());



    public void setAGENTID(String AGENTID) {
        this.AGENTID = AGENTID;
    }

    public void setDEVICEID(String DEVICEID) {
        this.DEVICEID = DEVICEID;
    }

    public void setPLATFORM(String PLATFORM) {
        this.PLATFORM = PLATFORM;
    }

    public void setTXID(String TXID) {
        this.TXID = TXID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

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
}
