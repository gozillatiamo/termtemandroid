package com.worldwidewealth.wealthcounter.model;

import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.Global;

/**
 * Created by MyNet on 9/11/2559.
 */

public class LogoutRequestModel {
    private String action = "LOGOUT";
    private Data data = new Data();

    public String getAction() {
        return action;
    }

    public static class Data{
        private String DEVICEID = Global.getDEVICEID();
        private String PLATFORM = Configs.getPLATFORM();
        private String TXID = Global.getTXID();
        private String AGENTID = Global.getAGENTID();
        private String USERID = Global.getUSERID();
    }

}
