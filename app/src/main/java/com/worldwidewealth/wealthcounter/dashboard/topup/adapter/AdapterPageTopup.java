package com.worldwidewealth.wealthcounter.dashboard.topup.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentChoiceTopup;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentListPackage;
import com.worldwidewealth.wealthcounter.model.LoadButtonResponseModel;
import com.worldwidewealth.wealthcounter.until.Until;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MyNet on 11/10/2559.
 */

public class AdapterPageTopup extends FragmentPagerAdapter {

    private String[] title = new String[]{
        "Airtime",
            "VAS"
    };

    private String mData;
    private List<LoadButtonResponseModel> mListAirtime;
    private List<LoadButtonResponseModel> mListVAS;

    public AdapterPageTopup(FragmentManager fm, String data) {
        super(fm);
        this.mData = data;
        mListAirtime = new ArrayList<>();
        mListVAS = new ArrayList<>();
        transferDataTopup();
    }

    private void transferDataTopup(){

        String converted = Until.ConvertJsonEncode(mData);
        String responDecode = Until.decode(converted);
        Log.e("strResponse", converted);
        Log.e("strDecode", responDecode);

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Until.JsonDateDeserializer()).create();
        Type listType = new TypeToken<List<LoadButtonResponseModel>>() {}.getType();
        List<LoadButtonResponseModel> modelList = (List<LoadButtonResponseModel>)  gson.fromJson(responDecode, listType);
        int i = 0;
        for (LoadButtonResponseModel model : modelList){
            Log.e(i+"",
                    "getCARRIER_CODE: " + model.getCARRIER_CODE() + "\n" +
                    "getCREATED_BY: " + model.getCREATED_BY() + "\n" +
                    "getPRODUCT_ITEM: " + model.getPRODUCT_ITEM() + "\n" +
                    "getPRODUCT_TYPE: " + model.getPRODUCT_TYPE() + "\n" +
                    "getTXID: " + model.getTXID() + "\n" +
                    "getUPDATE_BY: " + model.getUPDATE_BY() + "\n" +
                    "getACTIVE: " + model.getACTIVE() + "\n" +
                    "getCREATED_DATE: " + model.getCREATED_DATE() + "\n" +
                    "getPRODUCT_PRICE: " + model.getPRODUCT_PRICE() + "\n" +
                    "getSORT_NO: " + model.getSORT_NO());
            i++;

            switch (model.getPRODUCT_TYPE()){
                case "AIRTIME":
                    mListAirtime.add(model.getSORT_NO()-1, model);
                    break;
                case "VAS":
                    mListVAS.add(model.getSORT_NO()-1, model);
                    break;
            }
        }
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public FragmentChoiceTopup getItem(int position) {
        switch (position){
            case 0:
                return new FragmentChoiceTopup(mListAirtime);

            case 1:
                return new FragmentChoiceTopup(mListVAS);
        }

        return null;
    }
}
