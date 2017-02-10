package com.worldwidewealth.termtem.model;

import android.os.Parcelable;

import java.util.Date;

/**
 * Created by MyNet on 15/11/2559.
 */

public class LoadButtonResponseModel {
    private String TXID;
    private String CARRIER_CODE;
    private int SORT_NO;
    private String PRODUCT_TYPE;
    private String PRODUCT_ITEM;
    private Date CREATED_DATE;
    private String CREATED_BY;
    private Date UPDATR_DATE;
    private String UPDATE_BY;
    private int ACTIVE;
    private double PRODUCT_PRICE;

    public String getTXID() {
        return TXID;
    }

    public String getCARRIER_CODE() {
        return CARRIER_CODE;
    }

    public int getSORT_NO() {
        return SORT_NO;
    }

    public String getPRODUCT_TYPE() {
        return PRODUCT_TYPE;
    }

    public String getPRODUCT_ITEM() {
        return PRODUCT_ITEM;
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

    public double getPRODUCT_PRICE() {
        return PRODUCT_PRICE;
    }
}
