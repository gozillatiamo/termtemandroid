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
    private String AGENTIDCASHIN;
//    private String OTP;
    private String BUTTONID;

    public SubmitTopupRequestModel(String AMT, String CARRIER, String PHONENO, String TRANID, String BUTTONID, String AGENTIDCASHIN) {
        this.AMT = EncryptionData.EncryptData(AMT, Global.getInstance().getDEVICEID()+TRANID);

        if (CARRIER != null) {
            this.CARRIER = EncryptionData.EncryptData(CARRIER, Global.getInstance().getDEVICEID() + TRANID);
        } else {
            this.CARRIER = CARRIER;
        }

        this.AGENTIDCASHIN = AGENTIDCASHIN;

        this.PHONENO = EncryptionData.EncryptData(PHONENO, Global.getInstance().getDEVICEID()+TRANID);

        this.TRANID = TRANID;

        this.BUTTONID = BUTTONID;
    }
}
