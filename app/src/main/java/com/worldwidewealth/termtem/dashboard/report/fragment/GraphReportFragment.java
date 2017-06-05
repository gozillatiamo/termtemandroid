package com.worldwidewealth.termtem.dashboard.report.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.ChartResponseModel;

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

    private void setup(){

    }

    private void bindView(){

    }

    private void setupLineChart(){

    }

    private void setupPieChart(){

    }

    public void updateListDataLineChart(List<ChartResponseModel> listLineModel){
        mListLineModel = listLineModel;
    }

}
