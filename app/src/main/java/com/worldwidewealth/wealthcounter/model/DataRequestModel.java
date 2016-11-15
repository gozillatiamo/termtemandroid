package com.worldwidewealth.wealthcounter.model;

import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.Global;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DataRequestModel {

    private String DEVICEID = Global.getDEVICEID();
    private String PLATFORM = Configs.getPLATFORM();
    private String TXID = Global.getTXID();
    private String AGENTID = Global.getAGENTID();
    private String USERID = Global.getUSERID();

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
