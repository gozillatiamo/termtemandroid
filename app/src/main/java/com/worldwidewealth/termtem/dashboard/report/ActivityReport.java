package com.worldwidewealth.termtem.dashboard.report;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.dashboard.report.adapter.PagerTypeReportAdapter;
import com.worldwidewealth.termtem.dashboard.report.fragment.GraphReportFragment;
import com.worldwidewealth.termtem.dashboard.report.fragment.TextReportFragment;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.ChartResponseModel;
import com.worldwidewealth.termtem.model.ReportFundinResponse;
import com.worldwidewealth.termtem.widgets.BottomSheetTypeReport;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.adapter.ReportAdapter;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SalerptRequestModel;
import com.worldwidewealth.termtem.model.SalerptResponseModel;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.TermTemLoading;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityReport extends MyAppcompatActivity {

    private ViewHolder mHolder;
    private ReportAdapter mAdapter;
    private long mPreviousDateFrom;
    private long mPeviousDateTo;
    private Calendar mCalendar = Calendar.getInstance();
    private APIServices services;
    private DatePickerDialog mDatePickerDialog;
    private AlertDialog mChoiceDialog;
    private BottomSheetTypeReport mBottomSheet;
    private String mCurrentType;
    private boolean mCanCashIn;
    private boolean canShowTutorial = true;
    private TermTemLoading loading;
    private double mAmoutTopup, mAmountDebit;

    private static final int FORM = 0;
    private static final int TO = 1;

    public static final String TOPUP_REPORT = "TOPUP";
    public static final String EPIN_REPORT = "EPIN";
    public static final String CASHIN_REPORT = "CASHIN";
    public static final String FUNDIN_REPORT = "FUNDIN";
    public static final String VAS_REPORT = "VAS";
    public static final String BILL_REPORT = "BILL";

    public static final String TAG = ActivityReport.class.getSimpleName();
   // private Date mDate = new Date(mPreviousDateFrom);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mCanCashIn = getIntent().getExtras().getBoolean(CASHIN_REPORT);
        Log.e(TAG, "Dashboard Can CashIn: "+mCanCashIn);
        mHolder = new ViewHolder(this);
        services = APIServices.retrofit.create(APIServices.class);
        mPreviousDateFrom = Util.getTimestamp(System.currentTimeMillis(), 0, 0, 0);
        mPeviousDateTo = Util.getTimestamp(System.currentTimeMillis(), 23, 59, 59);
        initToolbar();
//        initListReport();
        setupViewPager();
        initBottomAction();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        if (mBottomSheet.getMenuSize() <= 1) {
            MenuItem menuItem = menu.findItem(R.id.action_swich_mode);
            menuItem.setVisible(false);
            invalidateOptionsMenu();
        }

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
            case R.id.action_swich_mode:
                mBottomSheet.show();
                mBottomSheet.setCancelable(true);
                mBottomSheet.setCanceledOnTouchOutside(true);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setEnableTotal(true);

        if (!getSupportFragmentManager().popBackStackImmediate()){
            super.onBackPressed();
        }
    }

    private void initBottomAction(){
        mBottomSheet = new BottomSheetTypeReport(ActivityReport.this);
        mCurrentType = mBottomSheet.getCurrentType();
        if (mBottomSheet.getMenuSize() > 1) {

            mBottomSheet.setOnResultTypeListener(new BottomSheetTypeReport.OnResultTypeListener() {
                @Override
                public void onResult(String typeReport) {
                    mCurrentType = typeReport;
                    Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager_type_history_report + ":" + 1);
                    ((GraphReportFragment)page).updateListDataLineChart(null);

                    if (mCurrentType.equals(ActivityReport.CASHIN_REPORT) ||
                            mCurrentType.equals(ActivityReport.FUNDIN_REPORT)){
                        ((GraphReportFragment)page).hidePieChart();
                    } else {
                        ((GraphReportFragment) page).showPieChart();
                    }

                    mPreviousDateFrom = Util.getTimestamp(System.currentTimeMillis(), 0, 0, 0);
                    mPeviousDateTo = Util.getTimestamp(System.currentTimeMillis(), 23, 59, 59);
                }
            });

            mBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showTargetView();
                }
            });

            mBottomSheet.show();
        } else {
            showTargetView();
        }

    }

    private void showTargetView(){
        if (canShowTutorial) {
            TapTargetView.showFor(ActivityReport.this,
                    TapTarget.forToolbarMenuItem(mHolder.mToolbar,
                            R.id.action_search,
                            getString(R.string.search),
                            getString(R.string.search_tutorial))
                            .outerCircleColor(R.color.colorPrimary)
                            .dimColor(android.R.color.black)
                            .drawShadow(true)
                            .transparentTarget(true)
                            .targetCircleColor(android.R.color.black), new TapTargetView.Listener(){
                        @Override
                        public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                            super.onTargetDismissed(view, userInitiated);
                            salerptService(mPreviousDateFrom + "", mPeviousDateTo + "");

                        }
                    });
            canShowTutorial = false;
        } else {
            salerptService(mPreviousDateFrom + "", mPeviousDateTo + "");
        }
    }


    private void initToolbar(){
        mHolder.mToolbar.inflateMenu(R.menu.menu_report);
        this.setSupportActionBar(mHolder.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

/*
    private void initListReport(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReportAdapter(this, null);
        mHolder.mListReport.setLayoutManager(layoutManager);
        mHolder.mListReport.setAdapter(new AlphaInAnimationAdapter(mAdapter));
//        mHolder.mListReport.addItemDecoration(new SimpleDividerItemDecoration(this));
    }
*/

    private void setupViewPager(){
        mHolder.mPagerTypeReport.setAdapter(new PagerTypeReportAdapter(getSupportFragmentManager(), this));
        mHolder.mTabTypeReport.setupWithViewPager(mHolder.mPagerTypeReport);
        mHolder.mPagerTypeReport.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case PagerTypeReportAdapter.TEXT:
                        setAmountTotal(position, mAmoutTopup, mAmountDebit);
                        break;
                    case PagerTypeReportAdapter.GRAPH:
                        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager_type_history_report + ":" + position);
                        setAmountTotal(position, ((GraphReportFragment)page).getAmountTopup(), 0);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        mChoiceDialog.setOnShowListener(new MyShowListener());
        mChoiceDialog.show();
    }

    private void salerptService(String timeFrom, String timeTo){
        if (mCurrentType == null) return;
        switch (mCurrentType){
            case TOPUP_REPORT:
                mHolder.mIconType.setImageResource(R.drawable.ic_report_topup);
                mHolder.mTextType.setText(R.string.topup);
                mHolder.mLogoIcon.setImageResource(R.drawable.ic_report_topup);
                break;
            case EPIN_REPORT:
                mHolder.mIconType.setImageResource(R.drawable.ic_report_epin);
                mHolder.mTextType.setText(R.string.dashboard_pin);
                mHolder.mLogoIcon.setImageResource(R.drawable.ic_report_epin);
                break;

            case VAS_REPORT:
                mHolder.mIconType.setImageResource(R.drawable.ic_vas_report);
                mHolder.mTextType.setText(R.string.vas);
                mHolder.mLogoIcon.setImageResource(R.drawable.ic_vas_report);
                break;

            case CASHIN_REPORT:
                mHolder.mIconType.setImageResource(R.drawable.ic_report_cashin);
                mHolder.mTextType.setText(R.string.add_credit_agent);
                mHolder.mLogoIcon.setImageResource(R.drawable.ic_report_cashin);
                break;

            case BILL_REPORT:
                mHolder.mIconType.setImageResource(R.drawable.ic_report_bill);
                mHolder.mTextType.setText(R.string.dashboard_bill_pay);
                mHolder.mLogoIcon.setImageResource(R.drawable.ic_report_bill);
                mHolder.mTextTitleTotalAmount.setText(R.string.amount_bill_total);
                break;

            case FUNDIN_REPORT:
                mHolder.mIconType.setImageResource(R.drawable.ic_my_cashin_report);
                mHolder.mTextType.setText(R.string.header_report_cashin);
                mHolder.mLogoIcon.setImageResource(R.drawable.ic_my_cashin_report);

                break;

        }

        mHolder.mLogoIcon.setVisibility(View.VISIBLE);

        findViewById(R.id.card_title_type).setVisibility(View.VISIBLE);

        if (loading == null)
            loading = new TermTemLoading(this, (ViewGroup) findViewById(R.id.activity_report));

        loading.show();

        serviceReportText(timeFrom, timeTo);
        serviceReportChart(timeFrom, timeTo);

/*
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeFrom));
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month-1);
*/

//        new DialogCounterAlert.DialogProgress(this).show();
    }

    private void serviceReportText(final String timeFrom, final String timeTo){
        Call<ResponseBody> call = services.salerpt(
                new RequestModel(APIServices.ACTIONSALERPT,
                        new SalerptRequestModel(timeFrom, timeTo, mCurrentType)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(ActivityReport.this, call, response.body(), this);
                if (responseValues == null) return;

                if (!(responseValues instanceof ResponseModel)){
                    loading.hide();

                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    List modelList = null;

                    switch (mCurrentType){
                        case FUNDIN_REPORT:
                            modelList = gson
                                    .fromJson((String)responseValues,
                                            new TypeToken<ArrayList<ReportFundinResponse>>(){}.getType());
                            break;
                        default:
                            modelList = gson
                                    .fromJson((String)responseValues,
                                            new TypeToken<ArrayList<SalerptResponseModel>>(){}.getType());
                            break;
                    }


                    double total = 0;
                    double debit = 0;

                    findViewById(R.id.txt_not_found_report).setVisibility(View.GONE);

                    if (modelList.size() == 0){
                        findViewById(R.id.txt_not_found_report).setVisibility(View.VISIBLE);
                    } else if (!mCurrentType.equals(FUNDIN_REPORT)){

                        switch (mCurrentType){
                            case BILL_REPORT:
                                for (Object model : modelList){
                                    SalerptResponseModel responseModel = (SalerptResponseModel) model;
                                    total += (responseModel.getAMOUNT()+responseModel.getCALFEE());
                                    debit += responseModel.getCHECKTOTAL();
                                }

                                break;
                            default:
                                for (Object model : modelList){
                                    SalerptResponseModel responseModel = (SalerptResponseModel) model;
                                    total += responseModel.getCHECKTOTAL();
                                    debit += responseModel.getAMOUNT();
                                }

                                break;
                        }

                        mAmoutTopup = total;
                        mAmountDebit = debit;
                        setAmountTotal(PagerTypeReportAdapter.TEXT, mAmoutTopup, mAmountDebit);

                    } else {
                        for (Object model : modelList){
                            ReportFundinResponse responseModel = (ReportFundinResponse) model;
                            total += responseModel.getCREDIT();
                        }

                        mAmoutTopup = total;
                        mAmountDebit = 0;
                        setAmountTotal(PagerTypeReportAdapter.TEXT, mAmoutTopup, mAmountDebit);

                    }

                    updateListData(PagerTypeReportAdapter.TEXT, modelList, null, Long.parseLong(timeFrom), Long.parseLong(timeTo));


                } else {
                    loading.hide();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.hide();
                new ErrorNetworkThrowable(t).networkError(ActivityReport.this, call, this);
            }
        });

    }

    public void setEnableTotal(boolean enable){
        if (enable){
            mHolder.mLayoutTotal.setVisibility(View.VISIBLE);
        } else
            mHolder.mLayoutTotal.setVisibility(View.GONE);
    }

    private void setAmountTotal(int typePage, double amountTopup, double amonutDebit){
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        if (!mCurrentType.equals(FUNDIN_REPORT)) {
            final View layoutAmountDebit = findViewById(R.id.layout_amount_debit);
            int heightView = layoutAmountDebit.getMeasuredHeight();
            switch (typePage) {
                case PagerTypeReportAdapter.TEXT:
                    mHolder.mLayoutTotal.animate()
                            .setDuration(300)
                            .translationY(0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    layoutAmountDebit.setVisibility(View.VISIBLE);
                                }
                            }).start();
                    mHolder.mTextDebitTotal.setText(format.format(amonutDebit));

                    break;
                case PagerTypeReportAdapter.GRAPH:
                    mHolder.mLayoutTotal.animate()
                            .setDuration(300)
                            .translationY(heightView)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutAmountDebit.setVisibility(View.INVISIBLE);
                                }
                            }).start();

                    break;
            }

        }

        mHolder.mTextReportTotal.setText(format.format(amountTopup));

    }


    private void serviceReportChart(String timeFrom, final String timeTo){
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager_type_history_report + ":" + 1);
        if (page == null) return;

        if (((GraphReportFragment)page).getmListLineModel() == null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeFrom));
            int month = calendar.get(Calendar.MONTH);
            calendar.set(Calendar.MONTH, month-1);
            timeFrom = String.valueOf(calendar.getTimeInMillis());
        }

        Call<ResponseBody> callLine = services.salerpt(
                new RequestModel(APIServices.ACTION_LINE_CHART,
                        new SalerptRequestModel(timeFrom, timeTo, mCurrentType)));
        final String finalTimeFrom = timeFrom;
        APIHelper.enqueueWithRetry(callLine, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(ActivityReport.this, call, response.body(), this);
                if (responseValues == null) return;

                if (!(responseValues instanceof ResponseModel)){
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    List<ChartResponseModel> modelList = gson
                            .fromJson((String)responseValues,
                                    new TypeToken<ArrayList<ChartResponseModel>>(){}.getType());

                    updateListData(PagerTypeReportAdapter.GRAPH, modelList, GraphReportFragment.LINE, Long.parseLong(finalTimeFrom), Long.parseLong(timeTo));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.hide();
            }
        });

        if(mCurrentType.equals(FUNDIN_REPORT)) return;

        Call<ResponseBody> callPie = services.salerpt(
                new RequestModel(APIServices.ACTION_PIE_CHART,
                        new SalerptRequestModel(timeFrom, timeTo, mCurrentType)));
        APIHelper.enqueueWithRetry(callPie, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(ActivityReport.this, call, response.body(), this);
                if (responseValues == null) return;

                if (!(responseValues instanceof ResponseModel)){

                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    List<ChartResponseModel> modelList = gson
                            .fromJson((String)responseValues,
                                    new TypeToken<ArrayList<ChartResponseModel>>(){}.getType());

                    updateListData(PagerTypeReportAdapter.GRAPH, modelList, GraphReportFragment.PIE, Long.parseLong(finalTimeFrom), Long.parseLong(timeTo));

                    loading.hide();

                } else {
                    loading.hide();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.hide();
            }
        });



    }

    private void updateListData(int position, List listdata, String chartType, long timefrom, long timeto){
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager_type_history_report + ":" + position);

        if (page == null) return;

        switch (position){
            case 0:
                ((TextReportFragment)page).updateDataReport(listdata);
                break;
            case 1:
                    ((GraphReportFragment) page).setRangeTime(timefrom, timeto);
                    switch (chartType) {
                        case GraphReportFragment.LINE:
                            ((GraphReportFragment) page).updateListDataLineChart(listdata);
                            break;
                        case GraphReportFragment.PIE:
                            ((GraphReportFragment) page).updatePieDataLineChart(listdata);
                            break;
                    }

                break;
        }

        mHolder.mPagerTypeReport.setCurrentItem(0);
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
                        mPreviousDateFrom = Util.getTimestamp(calendar.getTimeInMillis(), 0, 0, 0);
                        break;
                    case TO:
                        mPeviousDateTo = Util.getTimestamp(calendar.getTimeInMillis(), 23, 59, 59);
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

    public String getmCurrentType() {
        return mCurrentType;
    }

    private class ViewHolder{
//        private RecyclerView mListReport;
        private Toolbar mToolbar;
        private TextView mTextReportTotal, mTextDebitTotal, mTextType, mTextTitleTotalAmount;
        private ImageView mIconType;
        private ImageView mLogoIcon;
        private TabLayout mTabTypeReport;
        private ViewPager mPagerTypeReport;
        private View mLayoutTotal;
        public ViewHolder(Activity itemView){
//            mListReport = (RecyclerView) itemView.findViewById(R.id.list_report);
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
            mTextReportTotal = (TextView) itemView.findViewById(R.id.txt_report_total);
            mTextDebitTotal = (TextView) itemView.findViewById(R.id.txt_debit_total);
            mTextType = (TextView) itemView.findViewById(R.id.text_type_report);
            mIconType = (ImageView) itemView.findViewById(R.id.icon_type_report);
            mLogoIcon = (ImageView) itemView.findViewById(R.id.logo_menu);
            mTabTypeReport = (TabLayout) itemView.findViewById(R.id.tab_type_history_report);
            mPagerTypeReport = (ViewPager) itemView.findViewById(R.id.pager_type_history_report);
            mLayoutTotal = findViewById(R.id.layout_totle);
            mTextTitleTotalAmount = itemView.findViewById(R.id.text_title_total);

        }
    }
}
