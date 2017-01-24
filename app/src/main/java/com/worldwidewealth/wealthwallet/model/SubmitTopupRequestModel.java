package com.worldwidewealth.wealthwallet.model;


import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.Global;

/**
 * Created by MyNet on 22/11/2559.
 */

public class SubmitTopupRequestModel extends DataRequestModel{
    private String CARRIER;
    private String AMT;
    private String PHONENO;
    private String TRANID;
    private String OTP;
    private String BUTTONID;

    public SubmitTopupRequestModel(String AMT, String CARRIER, String OTP, String PHONENO, String TRANID, String BUTTONID) {
        this.AMT = EncryptionData.EncryptData(AMT, Global.getDEVICEID()+TRANID);

        this.CARRIER = EncryptionData.EncryptData(CARRIER, Global.getDEVICEID()+TRANID);

        String otpDecrypt = EncryptionData.DecryptData(Global.getOTP(),
                EncryptionData.DecryptData(Global.getAGENTID(), Global.getTXID())+TRANID);
        this.OTP = EncryptionData.EncryptData(otpDecrypt, Global.getDEVICEID()+TRANID);

        this.PHONENO = EncryptionData.EncryptData(PHONENO, Global.getDEVICEID()+TRANID);

        this.TRANID = TRANID;
        this.BUTTONID = BUTTONID;
    }
}
