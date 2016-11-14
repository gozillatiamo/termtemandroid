package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by MyNet on 14/11/2559.
 */

public class FragmentChoiceTopup extends Fragment{

    private GridView mGrid;
    private int previousSelectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mGrid = new GridView(getActivity());
        initGrid();
        return mGrid;
    }

    private void initGrid(){
        mGrid.setNumColumns(3);
        mGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 12;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =  LayoutInflater.from(getContext()).inflate(R.layout.item_topup, parent, false);
                return view;
            }
        });

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textPrice = (TextView) view.findViewById(R.id.txt_price);
                TextView textCurrency = (TextView) view.findViewById(R.id.txt_currency);

                textPrice.setTextColor(getResources().getColor(R.color.colorPrimary));
                textCurrency.setTextColor(getResources().getColor(R.color.colorPrimary));


                View previousSelectedView = (View) mGrid.getChildAt(previousSelectedPosition);

                if (previousSelectedPosition != -1){
                    textPrice = (TextView) previousSelectedView.findViewById(R.id.txt_price);
                    textCurrency = (TextView) previousSelectedView.findViewById(R.id.txt_currency);

                    textPrice.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                    textCurrency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));

                }

                previousSelectedPosition = position;

            }
        });

    }
}
