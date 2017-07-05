package com.worldwidewealth.termtem.dashboard.billpayment.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.billpayment.adapter.BillPayMenuAdapter;
import com.worldwidewealth.termtem.util.RecyclerItemClickListener;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainBillPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainBillPayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters

    private RecyclerView mRecyclerMenu;
    private BillPayMenuAdapter mAdapter;

    public static final int MAIN_MENU = 0x00;
    public static final int SUB_MENU = 0x01;



    public MainBillPayFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainBillPayFragment newInstance() {
        MainBillPayFragment fragment = new MainBillPayFragment();
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
        return inflater.inflate(R.layout.fragment_main_bill_pay, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindView();
        setupRecyclearView();
    }

    private void bindView(){
        mRecyclerMenu = (RecyclerView) getView().findViewById(R.id.recycler_bill_service);
    }

    private void setupRecyclearView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        String[] titleMenu = getResources().getStringArray(R.array.list_main_bill_service);
        mAdapter = new BillPayMenuAdapter(getContext(), titleMenu);
        mRecyclerMenu.setLayoutManager(gridLayoutManager);
        mRecyclerMenu.setAdapter(new ScaleInAnimationAdapter(mAdapter));
        mRecyclerMenu.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        }));
    }
}
