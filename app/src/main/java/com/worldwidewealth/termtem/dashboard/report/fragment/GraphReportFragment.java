package com.worldwidewealth.termtem.dashboard.report.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.ChartResponseModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private LineChart mLineChart = null;

    private List<ChartResponseModel> mListLineModel;
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
    }

    private void setupLineChart(){


        mLineChart.getDescription().setEnabled(false);
        mLineChart.setDrawGridBackground(false);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        // set data
        mLineChart.setData(generateDataLine());

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        mLineChart.animateX(750);

    }

    private LineData generateDataLine() {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (ChartResponseModel model : mListLineModel) {
            e1.add(new Entry(e1.size(), (float) model.getAMOUNT()));
        }

        LineDataSet d1 = new LineDataSet(e1, "New DataSet ");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        LineData cd = new LineData(sets);
        return cd;
    }


    private void setupPieChart(){

    }

    public void updateListDataLineChart(List<ChartResponseModel> listLineModel){
        mListLineModel = listLineModel;
        setupLineChart();
    }

    public LineChart getmLineChart() {
        return mLineChart;
    }
}
