package com.worldwidewealth.termtem.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by user on 02-Feb-17.
 */

public class TermTemDialog {

    public static class SearchDateRangeDialog{

        private Calendar mCalendar = Calendar.getInstance();
        private DatePickerDialog mDatePickerDialog;
        private Context mContext;
        private long mPreviousDateFrom;
        private long mPeviousDateTo;
        private AlertDialog mChoiceDialog;

        private static final int FORM = 0;
        private static final int TO = 1;


        public SearchDateRangeDialog(Context context, DialogInterface.OnClickListener submitListener){
            this.mContext = context;
            mPreviousDateFrom = getTimestamp(System.currentTimeMillis(), 0);
            mPeviousDateTo = getTimestamp(System.currentTimeMillis(), 23);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView  = inflater.inflate(R.layout.dialog_search_report, null);

            Button btnDateForm = (Button)dialogView.findViewById(R.id.btn_date_form);
            Button btnDateTo = (Button)dialogView.findViewById(R.id.btn_date_to);

            mCalendar.setTimeInMillis(System.currentTimeMillis());
            btnDateForm.setText(
                    mCalendar.get(Calendar.DAY_OF_MONTH) + "/" +
                            (mCalendar.get(Calendar.MONTH)+1) + "/" +
                            mCalendar.get(Calendar.YEAR));

//            mCalendar.setTimeInMillis(mPeviousDateTo);
            btnDateTo.setText(
                    mCalendar.get(Calendar.DAY_OF_MONTH) + "/" +
                            (mCalendar.get(Calendar.MONTH)+1) + "/" +
                            mCalendar.get(Calendar.YEAR));


            btnDateForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDatePickerDialog(mPreviousDateFrom, (Button) v, FORM);
                }
            });

            btnDateTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDatePickerDialog(mPeviousDateTo, (Button) v, TO);
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialogView);
            builder.setCancelable(false);
            builder.setTitle(R.string.search);
            builder.setPositiveButton(R.string.confirm, submitListener);
            builder.setNegativeButton(R.string.cancel, null);

            mChoiceDialog = builder.create();
        }

        public void show(){
            mChoiceDialog.show();
        }

        public boolean canSearch(){

            if (mPreviousDateFrom > mPeviousDateTo){
                new AlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage(R.string.error_date_limit)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChoiceDialog.show();
                                return;
                            }
                        }).show();
                return false;
            }

            return true;

        }

        private void initDatePickerDialog(long longdate, final Button btn, final int type){
            mCalendar.setTimeInMillis(longdate);
            mDatePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                    if (calendar.getTimeInMillis() > mDatePickerDialog.getDatePicker().getMaxDate() ||
                            calendar.getTimeInMillis() < mDatePickerDialog.getDatePicker().getMinDate()){
                        new AlertDialog.Builder(mContext)
                                .setCancelable(false)
                                .setMessage(R.string.error_date_limit)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDatePickerDialog.show();
                                        return;
                                    }
                                }).show();
                    }

                    btn.setText(dayOfMonth+"/"+(month+1)+"/"+ year);
                    switch (type){
                        case FORM:
                            mPreviousDateFrom = getTimestamp(calendar.getTimeInMillis(), 0);
                            break;
                        case TO:
                            mPeviousDateTo = getTimestamp(calendar.getTimeInMillis(), 23);
                            break;
                    }

                }
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            if (type == TO){
                mDatePickerDialog.getDatePicker().setMinDate(mPreviousDateFrom);
            }
            mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            mDatePickerDialog.setCancelable(false);
            mDatePickerDialog.show();
        }

        private long getTimestamp(long timestamp, int hourOfDay){
            mCalendar.setTimeInMillis(timestamp);
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            return mCalendar.getTimeInMillis();
        }


    }


}
