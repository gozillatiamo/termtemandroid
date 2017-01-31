package com.worldwidewealth.termtem.dashboard.addcreditline.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.FragmentTopupPreview;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;

import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddCreditChoice extends Fragment {

    private double mPrice = 0;
    private String[] mAmount = {
        "50",
        "100",
        "150",
        "200",
        "250",
        "300",
        "400",
        "500",
        "600",
        "800",
        "900",
        "Other Amount"
    };
    private ViewHolder mHolder;
    private int mPreviousItemClick = -1;
    public FragmentAddCreditChoice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_credit_choice, container, false);
        mHolder = new ViewHolder(rootView);
        initGrid();
        initBtn();
        return rootView;
    }

    public void onBackpress(){
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    if (event.getAction() == KeyEvent.ACTION_UP){
                        mHolder.mBtnNext.setVisibility(View.VISIBLE);
                        mHolder.mBtnTopup.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });

    }

    public void clearData(){
        mPreviousItemClick = -1;
        setPrice(0);
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrice == 0){
                    Toast.makeText(getContext(), R.string.please_choice_topup, Toast.LENGTH_LONG).show();
                    return;
                }
                onBackpress();
                FragmentTransaction transaction = getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container_add_credit_choice, FragmentTopupPreview.newInstance(null))
                        .addToBackStack(null);
                transaction.commit();
                mHolder.mBtnNext.setVisibility(View.GONE);
                mHolder.mBtnTopup.setVisibility(View.VISIBLE);

            }
        });

        mHolder.mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left)
                        .replace(R.id.container_add_credit, FragmentTopupSlip.newInstance(null, null))
                        .commit();
            }
        });
    }

    private void initGrid(){
        mHolder.mGridAmount.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mAmount.length;
            }

            @Override
            public Object getItem(int position) {
                return mAmount[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_topup, parent, false);
                TextView textPrice = (TextView)view.findViewById(R.id.txt_product_item);
                TextView textCurrency = (TextView) view.findViewById(R.id.txt_currency);

                textPrice.setText(mAmount[position]);
                if (position == mAmount.length-1){
                    textCurrency.setVisibility(View.INVISIBLE);
                }
                return view;
            }
        });

        mHolder.mGridAmount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setOnItemClickGrid(parent, view, position);
            }
        });
    }

    private void setOnItemClickGrid(AdapterView<?> parent, View view, int position){

        CardView btnChoice;
        TextView textPrice, textCurrency;
        double amount = 0;

        if (position > -1){
            if (mPreviousItemClick == position) return;
            if (position == mAmount.length-1){

                FragmentTransaction transaction = getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                        .add(R.id.container_add_credit_choice, new FragmentOtherAmount())
                        .addToBackStack(null);
                transaction.commit();
            } else {
                btnChoice = (CardView) view.findViewById(R.id.btn_choice);
                textPrice = (TextView) view.findViewById(R.id.txt_product_item);
                textCurrency = (TextView) view.findViewById(R.id.txt_currency);

                btnChoice.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textPrice.setTextColor(getResources().getColor(android.R.color.white));
                textCurrency.setTextColor(getResources().getColor(android.R.color.white));

                amount = Double.parseDouble(mAmount[position]);
            }

            setPrice(amount);

            if (mPreviousItemClick > -1){
                View previousView = parent.getChildAt(mPreviousItemClick);

                btnChoice = (CardView) previousView.findViewById(R.id.btn_choice);
                textPrice = (TextView) previousView.findViewById(R.id.txt_product_item);
                textCurrency = (TextView) previousView.findViewById(R.id.txt_currency);

                textPrice.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                textCurrency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                btnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            }

            mPreviousItemClick = position;
        }

    }

    public void setPrice(double amout){
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        mHolder.mTextPrice.setText(format.format(amout));
        mPrice = amout;
    }

    private class ViewHolder{
        private Button mBtnNext, mBtnTopup;
        private GridView mGridAmount;
        private TextView mTextPrice;
        public ViewHolder(View itemView) {
            mBtnNext = (Button) itemView.findViewById(R.id.btn_next);
            mGridAmount = (GridView) itemView.findViewById(R.id.grid_amount);
            mTextPrice = (TextView) itemView.findViewById(R.id.text_price);
            mBtnTopup = (Button) itemView.findViewById(R.id.btn_topup);
        }
    }

}
