package com.worldwidewealth.termtem.dashboard.report.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.adapter.ReportAdapter;
import com.worldwidewealth.termtem.model.SalerptResponseModel;
import com.worldwidewealth.termtem.util.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String KEY_DATA = "data";

    private RecyclerView mListReport;
    private ReportAdapter mAdapter;

    private ArrayList<SalerptResponseModel> mListData;
    // TODO: Rename and change types of parameters

    public TextReportFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TextReportFragment newInstance() {
        TextReportFragment fragment = new TextReportFragment();
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
        return inflater.inflate(R.layout.fragment_text_report, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setup();
    }

    private void setup(){
        bindView();
        setupRecyclerView();
    }

    private void bindView(){
        mListReport = (RecyclerView) getView().findViewById(R.id.list_report);

    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReportAdapter(getContext(), null);
        mListReport.setLayoutManager(layoutManager);
        mListReport.setAdapter(new AlphaInAnimationAdapter(mAdapter));
    }

    public void updateDataReport(List<SalerptResponseModel> listdata){
        mAdapter.updateAll(listdata);
    }

}
