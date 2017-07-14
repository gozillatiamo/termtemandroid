package com.worldwidewealth.termtem.dashboard.billpayment.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.OtherMenuFragment;
import com.worldwidewealth.termtem.dashboard.billpayment.ScanBillActivity;
import com.worldwidewealth.termtem.dashboard.billpayment.adapter.BillPayMenuAdapter;
import com.worldwidewealth.termtem.util.RecyclerItemClickListener;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainBillPayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainBillPayFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters

    private RecyclerView mRecyclerMenu;
    private Button mBtnScan;
    private BillPayMenuAdapter mAdapter;
    private int mType;
    private String[] mData;

    public static final int MAIN_MENU = 0x00;
    public static final int SUB_MENU = 0x01;

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";



    public MainBillPayFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainBillPayFragment newInstance(int type, String[] data) {
        MainBillPayFragment fragment = new MainBillPayFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putStringArray(KEY_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(KEY_TYPE);
            mData = getArguments().getStringArray(KEY_DATA);
        } else {
            mType = MAIN_MENU;
            mData = getResources().getStringArray(R.array.list_main_bill_service);
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
        mBtnScan = (Button) getView().findViewById(R.id.btn_scan_barcode);

        mBtnScan.setOnClickListener(this);
        if (mType == SUB_MENU){
            getView().findViewById(R.id.layout_two_btn).setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclearView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter = new BillPayMenuAdapter(getContext(), mData);
        mRecyclerMenu.setLayoutManager(gridLayoutManager);
        mRecyclerMenu.setAdapter(new ScaleInAnimationAdapter(mAdapter));
        mRecyclerMenu.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (mType){
                    case MAIN_MENU:
                        clickMainBill(position);
                        break;
                    case SUB_MENU:
                        clickSubMainBill(position);
                        break;
                }
            }
        }));
    }


    private void clickMainBill(int position){
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment, MainBillPayFragment.newInstance(SUB_MENU, getResources().getStringArray(R.array.list_sub_bill_electricity)))
                .addToBackStack(null)
                .commit();
    }

    private void clickSubMainBill(int position){

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan_barcode:
                startActivity(new Intent(getContext(), ScanBillActivity.class));
                break;
        }
    }
}
