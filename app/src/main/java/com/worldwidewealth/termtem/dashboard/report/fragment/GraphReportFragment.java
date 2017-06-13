package com.worldwidewealth.termtem.dashboard.report.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.model.ChartResponseModel;
import com.worldwidewealth.termtem.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphReportFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String LINE = "line";
    public static final String PIE = "pie";

    private static final int SAME = 0;
    private static final int BEFOR = -1;
    private static final int AFTER = 1;

    public static final String TAG = GraphReportFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters

    private LineChart mLineChart;
    private PieChart mPieChart;
    private AppCompatImageButton mBtnLineChart, mBtnPieChart;
    private TextView mTextTimeFrom, mTextTimeTo;
    private String textCenterPieChart = "PIE CHART";
    private Typeface mTf;

    private long mTimeFrom;
    private long mTimeTo;

    private HashMap<Integer, String> xAxisValues;
    private ArrayList<Entry> mLineData;


    private List<ChartResponseModel> mListLineModel = null;
    private List<ChartResponseModel> mListPieModel = null;
    public GraphReportFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GraphReportFragment newInstance() {
        GraphReportFragment fragment = new GraphReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Mitr-Regular.ttf");
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph_report, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setup();
    }

    private void setup(){
        bindView();
    }

    private void bindView(){
        mLineChart = (LineChart) getView().findViewById(R.id.line_chart);
        mPieChart = (PieChart) getView().findViewById(R.id.pie_chart);
        mBtnLineChart = (AppCompatImageButton) getView().findViewById(R.id.btn_line_chart);
        mBtnPieChart = (AppCompatImageButton) getView().findViewById(R.id.btn_pie_chart);
        mTextTimeFrom = (TextView) getView().findViewById(R.id.text_timefrom);
        mTextTimeTo = (TextView) getView().findViewById(R.id.text_timeto);
        mBtnLineChart.setOnClickListener(this);
        mBtnPieChart.setOnClickListener(this);

        mBtnLineChart.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        mPieChart.setVisibility(View.GONE);

    }

    private void setupLineChart(){
        mLineChart.clear();

        if (mListLineModel.size() == 0){
            mLineChart.setNoDataText(getString(R.string.no_data_graph));
            mLineChart.setNoDataTextTypeface(mTf);
            mLineChart.setNoDataTextColor(R.color.colorAccent);
            mLineChart.invalidate();
            return;
        } else {
            mLineChart.setNoDataText("");
        }

        LineData data = generateDataLine();
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setDrawGridBackground(false);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTypeface(mTf);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisValues.get((int)value);
            }
        });

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTypeface(mTf);

        mLineChart.getAxisRight().setEnabled(false);
        // set data
        mLineChart.setData(data);

        // do not forget to refresh the chart
        // holder.chart.invalidate();

        mLineChart.animateX(750);
        mLineChart.invalidate();


    }

    private LineData generateDataLine() {

        mLineData = new ArrayList<Entry>();
        xAxisValues = new HashMap<>();
        long dateStart = mTimeFrom;
        Calendar calendar = Calendar.getInstance();

        for (ChartResponseModel model : mListLineModel) {
            calendar.setTime(model.getPAYMENT_DATE());
            addDateValues(dateStart, model.getPAYMENT_DATE().getTime());
            xAxisValues.put(mLineData.size(), calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH")));
            mLineData.add(new Entry(mLineData.size(), (float) model.getAMOUNT()));
            Log.e(TAG, "LineData: "+calendar.get(Calendar.DAY_OF_MONTH)+"/"
                    +calendar.get(Calendar.MONTH)+"/"
                    +calendar.get(Calendar.YEAR)+"\nAmount: "+(float) model.getAMOUNT());
            rollCalendar(calendar);
            dateStart = calendar.getTimeInMillis();
        }

        addDateValues(calendar.getTimeInMillis(), mTimeTo);

        LineDataSet d1 = new LineDataSet(mLineData, getString(R.string.all_total));
        d1.setLineWidth(2.5f);
        d1.setDrawCircleHole(false);
        d1.setDrawCircles(false);
        d1.setHighLightColor(R.color.colorPrimary);
        d1.setDrawValues(false);


        LineData cd = new LineData(d1);
        cd.setValueTypeface(mTf);
        return cd;
    }

    private void addDateValues(long dateStart, long dateEnd){
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(Util.getTimestamp(dateStart, 0, 0, 0));

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(Util.getTimestamp(dateEnd, 0, 0, 0));


        while (calendarStart.before(calendarEnd)){
            xAxisValues.put(mLineData.size(), calendarStart.get(Calendar.DAY_OF_MONTH)+"/"+calendarStart.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH")));
            mLineData.add(new Entry(mLineData.size(), (float) 0.00));
            Log.e(TAG, "LineData: "+calendarStart.get(Calendar.DAY_OF_MONTH)+"/"
                    +calendarStart.get(Calendar.MONTH)+"/"
                    +calendarStart.get(Calendar.YEAR)+"\nAmount: "+0.00);

            rollCalendar(calendarStart);

        }
    }

    private void rollCalendar(Calendar calendar){
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        calendar.roll(Calendar.DAY_OF_MONTH, true);

        if (currentDay == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
            calendar.roll(Calendar.MONTH, true);

            if (currentMonth == calendar.getActualMaximum(Calendar.MONTH)){
                calendar.roll(Calendar.YEAR, true);
            }

        }


    }

    private PieData generateDataPie() {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        for (ChartResponseModel model : mListPieModel) {
            entries.add(new PieEntry((float) model.getAMOUNT(), model.getCARRIER()));
        }

        PieDataSet d = new PieDataSet(entries, "");
        d.setSliceSpace(3f);
        d.setSelectionShift(5f);

        d.setValueLinePart1OffsetPercentage(80.f);
        d.setValueLinePart1Length(0.2f);
        d.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        d.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // space between slices
        d.setSliceSpace(2f);

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#b4cd32"));
        colors.add(Color.parseColor("#009edf"));
        colors.add(Color.parseColor("#df1400"));
        d.setColors(colors);
//        d.setColors(new ColorTemplate.);

        PieData cd = new PieData(d);
        cd.setValueFormatter(new PercentFormatter());
        cd.setValueTextSize(11f);
        cd.setValueTypeface(mTf);


        return cd;
    }



    private void setupPieChart(){

        mPieChart.clear();

        if (mListPieModel.size() == 0){
            mPieChart.setNoDataText(getString(R.string.no_data_graph));
            mPieChart.setNoDataTextTypeface(mTf);
            mPieChart.setNoDataTextColor(R.color.colorAccent);
            mPieChart.invalidate();
            return;
        } else {
            mPieChart.setNoDataText("");
        }


        mPieChart.getDescription().setEnabled(false);
        mPieChart.setHoleRadius(52f);
        mPieChart.setCenterTextTypeface(mTf);
        mPieChart.setEntryLabelTypeface(mTf);
        mPieChart.setTransparentCircleRadius(57f);

        if (getActivity() instanceof ActivityReport){
            switch (((ActivityReport)getActivity()).getmCurrentType()){
                case ActivityReport.TOPUP_REPORT:
                    textCenterPieChart = getString(R.string.topup);
                    break;
                case ActivityReport.EPIN_REPORT:
                    textCenterPieChart = getString(R.string.dashboard_pin);
                    break;
                case ActivityReport.CASHIN_REPORT:
                    textCenterPieChart = getString(R.string.add_credit_agent);
                    break;
            }
        }
        mPieChart.setCenterText(textCenterPieChart);
        mPieChart.setCenterTextSize(9f);
        mPieChart.setUsePercentValues(true);
        mPieChart.setExtraOffsets(5, 10, 50, 10);
        mPieChart.setDragDecelerationFrictionCoef(0.95f);

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);


        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.highlightValues(null);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);



        // set data
        mPieChart.setData(generateDataPie());

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYEntrySpace(0f);
        l.setTypeface(mTf);
        l.setYOffset(0f);

        // do not forget to refresh the chart
        // holder.chart.invalidate();

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mPieChart.invalidate();


    }

    public void updateListDataLineChart(List<ChartResponseModel> listLineModel){
        mBtnLineChart.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        mPieChart.setVisibility(View.GONE);

        mListLineModel = listLineModel;

        setupLineChart();
    }

    public void updatePieDataLineChart(List<ChartResponseModel> listPieModel){
        mListPieModel = listPieModel;
        if(mListPieModel == null) return;
        setupPieChart();
    }


    public List<ChartResponseModel> getmListLineModel() {
        return mListLineModel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_line_chart:
                mLineChart.clear();
                mLineChart.invalidate();

                setChangeChart(LINE);
                break;
            case R.id.btn_pie_chart:
                mPieChart.clear();
                mPieChart.invalidate();

                setChangeChart(PIE);
                break;
        }
    }

    private void setChangeChart(final String type){
        View showView = null, hideView = null;
        switch (type){
            case LINE:
                showView = mLineChart;
                hideView = mPieChart;
                mBtnLineChart.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                mBtnPieChart.setColorFilter(null);
                break;
            case PIE:
                showView = mPieChart;
                hideView = mLineChart;
                mBtnPieChart.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                mBtnLineChart.setColorFilter(null);

                break;
        }
        if (showView == null || mListLineModel == null) return;

        final View finalHideView = hideView;
        final View finalShowView = showView;
        hideView.animate()
                .setDuration(300)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finalHideView.setVisibility(View.GONE);
                        finalShowView.animate()
                                .setDuration(300)
                                .alpha(1.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        finalShowView.setAlpha(0.0f);
                                        finalShowView.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        switch (type){
                                            case LINE:
                                                setupLineChart();
                                                break;
                                            case PIE:
                                                setupPieChart();
                                                break;
                                        }
                                    }
                                }).start();
                    }
                }).start();

    }

    public void setRangeTime(long timefrom, long timeto){
        this.mTimeFrom = timefrom;
        this.mTimeTo = timeto;
        Calendar calendar = Calendar.getInstance(new Locale("TH"));

        calendar.setTimeInMillis(timefrom);

        mTextTimeFrom.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"
        +calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH"))+"/"
        +(calendar.get(Calendar.YEAR)+543));

        calendar.setTimeInMillis(timeto);

        mTextTimeTo.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"
                +calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH"))+"/"
                +(calendar.get(Calendar.YEAR)+543));
    }
}
