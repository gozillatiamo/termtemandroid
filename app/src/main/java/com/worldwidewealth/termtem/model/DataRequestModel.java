package com.worldwidewealth.termtem.model;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DataRequestModel {

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
