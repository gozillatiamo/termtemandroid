package com.worldwidewealth.termtem.dashboard.report.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
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

        mLineChart.setNoDataText("");
        mPieChart.setNoDataText("");
    }

    private void setupLineChart(){
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

        ArrayList<Entry> e1 = new ArrayList<Entry>();
        xAxisValues = new HashMap<>();
        for (ChartResponseModel model : mListLineModel) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(model.getPAYMENT_DATE());
            xAxisValues.put(e1.size(), calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH")));
            e1.add(new Entry(e1.size(), (float) model.getAMOUNT()));
        }

        LineDataSet d1 = new LineDataSet(e1, "New DataSet ");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(R.color.colorPrimary);
        d1.setDrawValues(false);


        LineData cd = new LineData(d1);
        cd.setValueTypeface(mTf);
        return cd;
    }

    private void addDateValues(){

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
        if(mListLineModel == null) return;
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
                setChangeChart(LINE);
                break;
            case R.id.btn_pie_chart:
                setChangeChart(PIE);
                break;
        }
    }

    private void setChangeChart(final String type){
        View showView = null, hideView = null;
        switch (type){
            case LINE:
                mLineChart.clear();
                mLineChart.invalidate();
                showView = mLineChart;
                hideView = mPieChart;
                mBtnLineChart.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimary));
                mBtnPieChart.setColorFilter(null);
                break;
            case PIE:
                mPieChart.clear();
                mPieChart.invalidate();
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
        +calendar.getDisplayName(Calendar.YEAR, Calendar.LONG, new Locale("TH")));

        calendar.setTimeInMillis(timeto);

        mTextTimeTo.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"
                +calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH"))+"/"
                +calendar.get(Calendar.YEAR));
    }
}
