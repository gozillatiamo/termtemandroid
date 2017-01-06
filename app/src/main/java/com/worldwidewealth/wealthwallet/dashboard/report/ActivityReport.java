package com.worldwidewealth.wealthwallet.dashboard.report;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.report.adapter.ReportAdapter;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.model.SalerptRequestModel;
import com.worldwidewealth.wealthwallet.model.SalerptResponseModel;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.SimpleDividerItemDecoration;
import com.worldwidewealth.wealthwallet.until.Until;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityReport extends AppCompatActivity {

    private ViewHolder mHolder;
    private ReportAdapter mAdapter;
    private long mPreviousDateFrom;
    private long mPeviousDateTo;
    private Calendar mCalendar = Calendar.getInstance();
    private APIServices services;
    private DatePickerDialog mDatePickerDialog;
    private AlertDialog mChoiceDialog;

    private static final int FORM = 0;
    private static final int TO = 1;
   // private Date mDate = new Date(mPreviousDateFrom);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mHolder = new ViewHolder(this);
        services = APIServices.retrofit.create(APIServices.class);
        mPreviousDateFrom = getTimestamp(System.currentTimeMillis(), 0);
        mPeviousDateTo = getTimestamp(System.currentTimeMillis(), 23);
        initToolbar();
        initListReport();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_search:
                initSearchDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long getTimestamp(long timestamp, int hourOfDay){
        mCalendar.setTimeInMillis(timestamp);
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        return mCalendar.getTimeInMillis();
    }

    private void initToolbar(){
        this.setSupportActionBar(mHolder.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListReport(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReportAdapter(this, null);
        mHolder.mListReport.setLayoutManager(layoutManager);
        mHolder.mListReport.setAdapter(new AlphaInAnimationAdapter(mAdapter));
        mHolder.mListReport.addItemDecoration(new SimpleDividerItemDecoration(this));
        salerptService(mPreviousDateFrom+"", mPeviousDateTo+"");
    }

    private void initSearchDialog(){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView  = inflater.inflate(R.layout.dialog_search_report, null);

        Button btnDateForm = (Button)dialogView.findViewById(R.id.btn_date_form);
        Button btnDateTo = (Button)dialogView.findViewById(R.id.btn_date_to);

        mCalendar.setTimeInMillis(mPreviousDateFrom);
        btnDateForm.setText(
                mCalendar.get(Calendar.DAY_OF_MONTH) + "/" +
                        (mCalendar.get(Calendar.MONTH)+1) + "/" +
                mCalendar.get(Calendar.YEAR));

        mCalendar.setTimeInMillis(mPeviousDateTo);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setTitle(R.string.search);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPreviousDateFrom > mPeviousDateTo){
                    new AlertDialog.Builder(ActivityReport.this)
                            .setCancelable(false)
                            .setMessage(R.string.error_date_limit)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mChoiceDialog.show();
                                    return;
                                }
                            }).show();

                }
                String timeFrom = (mPreviousDateFrom)+"";
                String timeTo = (mPeviousDateTo)+"";
                salerptService(timeFrom, timeTo);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        mChoiceDialog = builder.create();
        mChoiceDialog.show();
    }

    private void salerptService(String timeFrom, String timeTo){
        new DialogCounterAlert.DialogProgress(this);
        Call<ResponseBody> call = services.salerpt(
                new RequestModel(APIServices.ACTIONSALERPT,
                        new SalerptRequestModel(timeFrom, timeTo)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ContentValues responseValues = EncryptionData.getModel(response.body());
                if (responseValues.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    ResponseModel responseModel = new Gson().fromJson(responseValues.getAsString(EncryptionData.STRMODEL), ResponseModel.class);
                    DialogCounterAlert.DialogProgress.dismiss();

//                    new DialogCounterAlert(ActivityReport.this, null, responseModel.getMsg(), null);

                    if (responseModel.getStatus() != APIServices.SUCCESS)
                        new ErrorNetworkThrowable(null).networkError(ActivityReport.this,
                                responseModel.getMsg(), call, this);

                } else {
                    DialogCounterAlert.DialogProgress.dismiss();
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Until.JsonDateDeserializer()).create();
                    List<SalerptResponseModel> modelList = gson
                            .fromJson(responseValues.getAsString(EncryptionData.STRMODEL),
                                    new TypeToken<ArrayList<SalerptResponseModel>>(){}.getType());

                    NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
                    format.setMaximumFractionDigits(2);
                    format.setMinimumFractionDigits(2);
                    double total = 0;

                    if (modelList.size() == 0){
                        findViewById(R.id.txt_not_found_report).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.txt_not_found_report).setVisibility(View.GONE);


                        for (SalerptResponseModel model : modelList){
                            total += model.getCHECKTOTAL();
                        }
                    }

                    mHolder.mTextReportTotal.setText(format.format(total));
                    mAdapter.updateAll(modelList);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(ActivityReport.this, call, this);
            }
        });
    }

    private void initDatePickerDialog(long longdate, final Button btn, final int type){
        mCalendar.setTimeInMillis(longdate);
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                if (calendar.getTimeInMillis() > mDatePickerDialog.getDatePicker().getMaxDate() ||
                        calendar.getTimeInMillis() < mDatePickerDialog.getDatePicker().getMinDate()){
                    new AlertDialog.Builder(ActivityReport.this)
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



    private class ViewHolder{
        private RecyclerView mListReport;
        private Toolbar mToolbar;
        private TextView mTextReportTotal;

        public ViewHolder(Activity itemView){
            mListReport = (RecyclerView) itemView.findViewById(R.id.list_report);
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
            mTextReportTotal = (TextView) itemView.findViewById(R.id.txt_report_total);
        }
    }
}