package com.worldwidewealth.wealthcounter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;

/**
 * Created by MyNet on 10/10/2559.
 */

public class DialogBillAlert {

    private Context mContext;
    private Button mBtnCancel, mBtnConfirm;
    private Spinner mSpinner;
    private Dialog dialog;
    private LinearLayout mContainer;

    public DialogBillAlert(Context context){
        this.mContext = context;

        initDialog();
    }

    private void initDialog(){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bill_alert);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        mBtnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        mBtnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        mBtnConfirm.setVisibility(View.GONE);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        initSpinner();
    }

    private void initSpinner(){
        mContainer = (LinearLayout) dialog.findViewById(R.id.container_alert);
        mSpinner = (Spinner) dialog.findViewById(R.id.spinner_alert);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.bill_alert_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textTitle = new TextView(mContext);
                mContainer.removeAllViews();
                switch (position){
                    case 0:
                        mBtnConfirm.setVisibility(View.GONE);
                        mContainer.setVisibility(View.GONE);
                        break;
                    case 1:
                        NumberPicker pickerHour = new NumberPicker(mContext);
                        pickerHour.setMinValue(1);
                        pickerHour.setMaxValue(24);
                        mContainer.addView(pickerHour);
                        textTitle.setText("ชม.");
                        mContainer.addView(textTitle);
                        mBtnConfirm.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        NumberPicker pickerDay = new NumberPicker(mContext);
                        pickerDay.setMinValue(1);
                        pickerDay.setMaxValue(7);
                        mContainer.addView(pickerDay);
                        textTitle.setText("วัน");
                        mContainer.addView(textTitle);
                        mBtnConfirm.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.VISIBLE);

                        break;
                    case 3:
                        DatePicker datePicker = new DatePicker(mContext);
                        mContainer.addView(datePicker);
                        mBtnConfirm.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public void setOnClick(View.OnClickListener listener){
        mBtnConfirm.setOnClickListener(listener);
    }

}
