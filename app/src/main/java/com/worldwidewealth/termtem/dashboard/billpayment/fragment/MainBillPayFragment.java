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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.billpayment.ScanBillActivity;
import com.worldwidewealth.termtem.dashboard.billpayment.adapter.BillPayCategoryAdapter;
import com.worldwidewealth.termtem.dashboard.billpayment.adapter.BillPayServiceAdapter;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.LoadBillCategoryResponse;
import com.worldwidewealth.termtem.model.LoadBillService;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.RecyclerItemClickListener;
import com.worldwidewealth.termtem.util.Util;

import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private BillPayCategoryAdapter mCategoryAdapter;
    private BillPayServiceAdapter mServiceAdapter;
    private int mType;
    private String mBillCategoryID;
    private ArrayList<LoadBillCategoryResponse> mListBillCategory;
    private ArrayList<LoadBillServiceResponse> mListBillService;
    private APIServices services;


    public static final int MAIN_MENU = 0x00;
    public static final int SUB_MENU = 0x01;

    private static final String KEY_TYPE = "type";
    private static final String KEY_BILL_ID = "billid";
    public static final String KEY_BILL_DATA = "billdata";



    public MainBillPayFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainBillPayFragment newInstance(int type, String billpay_category_id) {
        MainBillPayFragment fragment = new MainBillPayFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putString(KEY_BILL_ID, billpay_category_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(KEY_TYPE);
            mBillCategoryID = getArguments().getString(KEY_BILL_ID);
        } else {
            mType = MAIN_MENU;
            mBillCategoryID = null;
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

        services = APIServices.retrofit.create(APIServices.class);
        bindView();
        getBillService();
    }

    private void bindView(){
        mRecyclerMenu = (RecyclerView) getView().findViewById(R.id.recycler_bill_service);
/*
        mBtnScan = (Button) getView().findViewById(R.id.btn_scan_barcode);

        mBtnScan.setOnClickListener(this);
        if (mType == SUB_MENU){
            getView().findViewById(R.id.layout_two_btn).setVisibility(View.VISIBLE);
        }
*/
    }

    private void getBillService(){
        RequestModel requestModel = null;
        switch (mType){
            case MAIN_MENU:
                requestModel = new RequestModel(APIServices.ACTION_LOAD_BILL_CATEGORY,
                        new DataRequestModel());
                break;
            case SUB_MENU:
                requestModel = new RequestModel(APIServices.ACTION_LOAD_BILL_SERVICE,
                        new LoadBillService(mBillCategoryID));
                break;
        }

        if (requestModel == null) return;

        Call<ResponseBody> call = services.billService(requestModel);
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (objectResponse instanceof String){
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();

                    switch (mType){
                        case MAIN_MENU:
                            mListBillCategory = gson
                                    .fromJson((String) objectResponse,
                                            new TypeToken<ArrayList<LoadBillCategoryResponse>>() {
                                            }.getType());
                            break;
                        case SUB_MENU:
                            mListBillService = gson
                                    .fromJson((String) objectResponse,
                                            new TypeToken<ArrayList<LoadBillServiceResponse>>() {
                                            }.getType());
                            break;
                    }

                    setupRecyclearView();


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
            }
        });
    }

    private void setupRecyclearView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerMenu.setLayoutManager(gridLayoutManager);

        switch (mType){
            case MAIN_MENU:
                mCategoryAdapter = new BillPayCategoryAdapter(this, mListBillCategory);
                mRecyclerMenu.setAdapter(new ScaleInAnimationAdapter(mCategoryAdapter));
                break;
            case SUB_MENU:
                mServiceAdapter = new BillPayServiceAdapter(this, mListBillService);
                mRecyclerMenu.setAdapter(new ScaleInAnimationAdapter(mServiceAdapter));
                break;
        }

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
                .replace(R.id.fragment, MainBillPayFragment.newInstance(SUB_MENU, mCategoryAdapter.getItem(position).getBILLPAY_CATEGORY_ID()))
                .addToBackStack(null)
                .commit();
    }

    private void clickSubMainBill(int position){
        LoadBillServiceResponse response = mServiceAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BILL_DATA, response);
        if (response.getSCAN() > 0){
            startActivityForResult(new Intent(getContext(), ScanBillActivity.class),
                    BillPaymentActivity.SCAN,
                    bundle);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
/*
            case R.id.btn_scan_barcode:
                startActivity(new Intent(getContext(), ScanBillActivity.class));
                break;
*/
        }
    }
}
