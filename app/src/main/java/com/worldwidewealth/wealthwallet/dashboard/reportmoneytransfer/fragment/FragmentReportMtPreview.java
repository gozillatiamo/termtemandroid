package com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment;

import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by MyNet on 17/10/2559.
 */

public class FragmentReportMtPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private String[] mListCodeBank;
    private TypedArray mListIconBank;
    private static final String AMOUNTMT = "amountmt";
    private static final String IMAGESLIP = "imageslip";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String BANKSTART = "bankstart";
    private static final String BANKEND = "bankend";
    public static Fragment newInstance(double amountMt, byte[] imageSlip, String date, String time, int bankstart, int bankend){
        FragmentReportMtPreview fragment = new FragmentReportMtPreview();
        Bundle bundle = new Bundle();
        bundle.putDouble(AMOUNTMT, amountMt);
        bundle.putByteArray(IMAGESLIP, imageSlip);
        bundle.putString(DATE, date);
        bundle.putString(TIME, time);
        bundle.putInt(BANKSTART, bankstart);
        bundle.putInt(BANKEND, bankend);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mListCodeBank = getResources().getStringArray(R.array.list_code_bank);
        mListIconBank = getContext().getResources().obtainTypedArray(R.array.ic_list_bank);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_report_mt_preview, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        initData();
        initBtn();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onBackPress();
    }

    private void initBtn(){
        mHolder.mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void onBackPress(){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) return true;

                return false;
            }
        });

    }


    private void initData(){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        mHolder.mTextAmoutMT.setText(format.format(getArguments().getDouble(AMOUNTMT)));
        byte[] byteImage = getArguments().getByteArray(IMAGESLIP);
        mHolder.mImageSlip.setImageBitmap(BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length));
        mHolder.mTextDate.setText(getArguments().getString(DATE));
        mHolder.mTextTime.setText(getArguments().getString(TIME));
        mHolder.mTextBankStart.setText(mListCodeBank[getArguments().getInt(BANKSTART)]);
        mHolder.mTextBankEnd.setText(mListCodeBank[getArguments().getInt(BANKEND)]);
        mHolder.mIconBankStart.setImageDrawable(mListIconBank.getDrawable(getArguments().getInt(BANKSTART)));
        mHolder.mIconBankEnd.setImageDrawable(mListIconBank.getDrawable(getArguments().getInt(BANKEND)));
    }

    public class ViewHolder{
        private Button mBtnDone;
        private ImageView mImageSlip, mIconBankStart, mIconBankEnd;
        private TextView mTextAmoutMT, mTextDate, mTextTime, mTextBankStart, mTextBankEnd;

        public ViewHolder(View itemview){

            mBtnDone = (Button)itemview.findViewById(R.id.btn_done);
            mImageSlip = (ImageView) itemview.findViewById(R.id.image_photo);
            mTextAmoutMT = (TextView) itemview.findViewById(R.id.txt_amount_mt);
            mIconBankStart = (ImageView) itemview.findViewById(R.id.icon_bank_start);
            mIconBankEnd = (ImageView) itemview.findViewById(R.id.icon_bank_end);
            mTextDate = (TextView) itemview.findViewById(R.id.txt_date);
            mTextTime = (TextView) itemview.findViewById(R.id.txt_time);
            mTextBankStart = (TextView) itemview.findViewById(R.id.txt_bank_start);
            mTextBankEnd = (TextView) itemview.findViewById(R.id.txt_bank_end);

        }
    }

}
