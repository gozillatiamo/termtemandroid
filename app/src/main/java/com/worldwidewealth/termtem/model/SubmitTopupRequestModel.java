package com.worldwidewealth.termtem.model;


import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;

/**
 * Created by MyNet on 22/11/2559.
 */

public class SubmitTopupRequestModel extends DataRequestModel{
    private String CARRIER;
    private String AMT;
    private String PHONENO;
    private String TRANID;
//    private String OTP;
    private String BUTTONID;

    public SubmitTopupRequestModel(String AMT, String CARRIER, String PHONENO, String TRANID, String BUTTONID) {
        this.AMT = EncryptionData.EncryptData(AMT, Global.getDEVICEID()+TRANID);

        this.CARRIER = EncryptionData.EncryptData(CARRIER, Global.getDEVICEID()+TRANID);


        this.PHONENO = EncryptionData.EncryptData(PHONENO, Global.getDEVICEID()+TRANID);

        this.TRANID = TRANID;
        this.BUTTONID = BUTTONID;
    }
}
