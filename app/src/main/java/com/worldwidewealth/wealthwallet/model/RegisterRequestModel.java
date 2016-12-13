package com.worldwidewealth.wealthwallet.model;

import com.worldwidewealth.wealthwallet.Configs;
import com.worldwidewealth.wealthwallet.Global;

/**
 * Created by MyNet on 10/11/2559.
 */

public class RegisterRequestModel {
    private String action = "REGISTER";
    private Data data;

    public RegisterRequestModel(Data data){
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public static class Data{
        private String AGENTID = "";
        private String USERID = "";
        private String DEVICEID = Global.getDEVICEID();
        private String PLATFORM = Configs.getPLATFORM();
        private String TXID = Global.getTXID();
        private String FIRSTNAME;
        private String LASTNAME;
        private String EMAIL;
        private String PHONENO;
        private String IDCARD;
        private int AGENTTYPE;

        public Data(String firstname,
                    String lastname,
                    String email,
                    String phoneno,
                    String idcard,
                    int agenttype){

            this.FIRSTNAME = firstname;
            this.LASTNAME = lastname;
            this.EMAIL = email;
            this.PHONENO = phoneno;
            this.IDCARD = idcard;
            this.AGENTTYPE = agenttype;

        }
    }
}
