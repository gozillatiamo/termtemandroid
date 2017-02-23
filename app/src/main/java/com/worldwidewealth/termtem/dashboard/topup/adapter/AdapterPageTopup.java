package com.worldwidewealth.termtem.dashboard.topup.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentChoiceTopup;
import com.worldwidewealth.termtem.model.LoadButtonResponseModel;
import com.worldwidewealth.termtem.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MyNet on 11/10/2559.
 */

public class AdapterPageTopup extends FragmentPagerAdapter {

    private String[] title;

    private String mData;
    private List<LoadButtonResponseModel> mListAirtime;
    private List<LoadButtonResponseModel> mListVAS;

    public AdapterPageTopup(FragmentManager fm, String data) {
        super(fm);
        this.mData = data;
        mListAirtime = new ArrayList<>();
        mListVAS = new ArrayList<>();
        title = new String[]{
                MyApplication.getContext().getString(R.string.airtime),
                MyApplication.getContext().getString(R.string.vas)
        };
        transferDataTopup();
    }

    private void transferDataTopup(){

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
        Type listType = new TypeToken<List<LoadButtonResponseModel>>() {}.getType();
        List<LoadButtonResponseModel> modelList = gson.fromJson(mData, listType);
        for (LoadButtonResponseModel model : modelList){

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
        return title.length-1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public FragmentChoiceTopup getItem(int position) {
        switch (position){
            case 0:
                return FragmentChoiceTopup.newInstance(mListAirtime);

            case 1:
                return FragmentChoiceTopup.newInstance(mListVAS);
        }

        return null;
    }
}
