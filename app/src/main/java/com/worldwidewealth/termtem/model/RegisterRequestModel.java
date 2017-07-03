package com.worldwidewealth.termtem.model;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.services.APIServices;

/**
 * Created by MyNet on 10/11/2559.
 */

public class RegisterRequestModel {
    private String action = APIServices.ACTION_REGISTER;
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
        private String DEVICEID = Global.getInstance().getDEVICEID();
        private String PLATFORM = MyApplication.getContext().getString(R.string.platform);
        private String TXID = Global.getInstance().getTXID();
        private String FIRSTNAME;
        private String LASTNAME;
        private String EMAIL;
        private String PHONENO;
        private String IDCARD;
        private String TITLE_NAME;
        private String REFCODE;
        private long BDAY;
        private String IDFILE;
        private int AGENTTYPE;

        public Data(String titlename,
                    String firstname,
                    String lastname,
                    long birthday,
                    String email,
                    String phoneno,
                    String idcard,
                    int agenttype,
                    String idfile){

            this.TITLE_NAME = titlename;
            this.FIRSTNAME = firstname;
            this.LASTNAME = lastname;
            this.BDAY  = birthday;
            this.EMAIL = email;
            this.PHONENO = phoneno;
            this.IDCARD = idcard;
            this.AGENTTYPE = agenttype;
            this.IDFILE = idfile;


        }

        public void setREFCODE(String REFCODE) {
            this.REFCODE = REFCODE;
        }
    }
}
