package com.worldwidewealth.wealthcounter.model;

import com.worldwidewealth.wealthcounter.EncryptionData;

/**
 * Created by MyNet on 1/11/2559.
 */

public class TestModel {

    private String data;

    public TestModel(String data){
        this.data = EncryptionData.EncryptData(data);
    }
}
