package com.worldwidewealth.termtem.model;

import java.util.Date;

/**
 * Created by user on 09-May-17.
 */

public class AttachResponseModel {
    private String TXID;
    private String MSGID;
    private String URLFILE;
    private String FILETYPE;
    private String CONTENTTYPE;
    private Double FILESIZE;
    private Date CREATED_DATE;
    private String CREATED_BY;
    private Date UPDATR_DATE;
    private String UPDATE_BY;
    private int ACTIVE;
    private int SORTNO;

    public String getTXID() {
        return TXID;
    }

    public String getMSGID() {
        return MSGID;
    }

    public String getURLFILE() {
        return URLFILE;
    }

    public String getFILETYPE() {
        return FILETYPE;
    }

    public String getCONTENTTYPE() {
        return CONTENTTYPE;
    }

    public Double getFILESIZE() {
        return FILESIZE;
    }

    public Date getCREATED_DATE() {
        return CREATED_DATE;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public Date getUPDATR_DATE() {
        return UPDATR_DATE;
    }

    public String getUPDATE_BY() {
        return UPDATE_BY;
    }

    public int getACTIVE() {
        return ACTIVE;
    }

    public int getSORTNO() {
        return SORTNO;
    }
}
