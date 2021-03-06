package com.worldwidewealth.termtem.dashboard.reportmoneytransfer.fragment;

import android.content.ContentValues;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.PopupChoiceBank;

import java.text.NumberFormat;

/**
 * Created by MyNet on 17/10/2559.
 */

public class FragmentReportMtPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
/*
    private String[] mListCodeBank;
    private TypedArray mListIconBank;
*/

    private ContentValues mValuesBankStart;
    private ContentValues mValuesBankEnd;
    private static final String AMOUNTMT = "amountmt";
    private static final String IMAGESLIP = "imageslip";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String BANKSTART = "bankstart";
    private static final String BANKEND = "bankend";
    public static Fragment newInstance(double amountMt, String imageSlip, String date, String time, ContentValues bankstart, ContentValues bankend){
        FragmentReportMtPreview fragment = new FragmentReportMtPreview();
        Bundle bundle = new Bundle();
        bundle.putDouble(AMOUNTMT, amountMt);
        bundle.putString(IMAGESLIP, imageSlip);
        bundle.putString(DATE, date);
        bundle.putString(TIME, time);
        bundle.putParcelable(BANKSTART, bankstart);
        bundle.putParcelable(BANKEND, bankend);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
/*
        mListCodeBank = getResources().getStringArray(R.array.list_code_bank);
        mListIconBank = getContext().getResources().obtainTypedArray(R.array.ic_list_bank);
*/
        mValuesBankStart = getArguments().getParcelable(BANKSTART);
        mValuesBankEnd = getArguments().getParcelable(BANKEND);

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
        String base64Image = getArguments().getString(IMAGESLIP);
        byte[] bytesImage = Base64.decode(base64Image, Base64.DEFAULT);
        mHolder.mImageSlip.setImageBitmap(BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length));
        mHolder.mTextDate.setText(getArguments().getString(DATE));
        mHolder.mTextTime.setText(getArguments().getString(TIME));
        mHolder.mTextBankStart.setText(mValuesBankStart.getAsString(PopupChoiceBank.KEY_CODE));
        mHolder.mTextBankEnd.setText(mValuesBankEnd.getAsString(PopupChoiceBank.KEY_CODE));
        mHolder.mIconBankStart.setImageResource(mValuesBankStart.getAsInteger(PopupChoiceBank.KEY_ICON));
        mHolder.mIconBankEnd.setImageResource(mValuesBankEnd.getAsInteger(PopupChoiceBank.KEY_ICON));
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
