package com.worldwidewealth.termtem.dashboard.billpayment.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.billpayment.ScanBillActivity;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.LoadBillRefRequest;
import com.worldwidewealth.termtem.model.LoadBillRefResponse;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
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
 * Use the {@link BillActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillActionFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match

    private LoadBillServiceResponse mResponse;

    private TextView mTextTitleService;
    private EditText mEditPhoneNo;
    private ImageView mLogoService;
    private Button mBtnScan, mBtnKeyIn, mBtnNext;
    private View mLayoutKeyIn;
    private RecyclerView mRecyclerRef;
    private APIServices services;
    private String mPhoneNo;

    public BillActionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BillActionFragment newInstance(LoadBillServiceResponse response) {
        BillActionFragment fragment = new BillActionFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainBillPayFragment.KEY_BILL_DATA, response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResponse = getArguments().getParcelable(MainBillPayFragment.KEY_BILL_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        services = APIServices.retrofit.create(APIServices.class);
        return inflater.inflate(R.layout.fragment_bill_action, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindView();
        setupData();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case BillPaymentActivity.SCAN:
                    LoadBillServiceResponse response = data.getExtras().getParcelable(MainBillPayFragment.KEY_BILL_DATA);
                    String result = data.getStringExtra(ScanBillActivity.KEY_SCAN_RESULT);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                            .replace(R.id.container_topup, FragmentTopupPackage.newInstanceBill(
                                    BillPaymentActivity.BILLPAY,
                                    result,
                                    response,
                                    mPhoneNo))
                            .commit();

                    break;
            }
        }

    }

    private boolean mFormatting;

    private void bindView(){
        mTextTitleService = getView().findViewById(R.id.text_title_service);
        mLogoService = getView().findViewById(R.id.img_service_logo);
        mBtnScan = getView().findViewById(R.id.btn_scan);
        mBtnKeyIn = getView().findViewById(R.id.btn_key_in);
        mBtnNext = getView().findViewById(R.id.btn_next);
        mLayoutKeyIn = getView().findViewById(R.id.layout_enter_ref);
        mRecyclerRef = getView().findViewById(R.id.recycler_reference);
        mEditPhoneNo = getView().findViewById(R.id.edit_phone_no);

        mEditPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mFormatting){
                    mFormatting = true;
                    PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.FORMAT_NANP);
                    mFormatting = false;
                }
            }
        });


        mBtnKeyIn.setOnClickListener(this);
        mBtnScan.setOnClickListener(this);
    }

    private void setupData(){
        mTextTitleService.setText(mResponse.getBILL_SERVICE_NAME());
        System.gc();
        Glide.with(this).load(getString(R.string.server)+mResponse.getLOGOURL())
                .placeholder(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)))
                .thumbnail(0.3f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(mLogoService);

        if (mResponse.getSCAN() == 0) mBtnScan.setVisibility(View.GONE);
        if (mResponse.getKEYIN() == 0) mBtnKeyIn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        mPhoneNo = mEditPhoneNo.getText().toString().replaceAll("-", "");
        if (mPhoneNo.length() < 10){
            Toast.makeText(getContext(), R.string.hint_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()){
            case R.id.btn_scan:
//                MyApplication.LeavingOrEntering.currentActivity = null;
                Intent intent = new Intent(getContext(), ScanBillActivity.class);
                intent.putExtra(MainBillPayFragment.KEY_BILL_DATA, mResponse);
                startActivityForResult(intent,
                        BillPaymentActivity.SCAN);

                break;
            case R.id.btn_key_in:
                mBtnKeyIn.animate()
                        .setDuration(500)
                        .alpha(0f).start();
                mLogoService.animate()
                        .setDuration(500)
                        .alpha(0.2f).start();
                setupRecycler();
                break;
        }
    }

    private void setupRecycler(){
        mBtnNext.setAlpha(0f);
        mBtnNext.setVisibility(View.VISIBLE);
        mBtnNext.animate()
                .setDuration(500)
                .alpha(1f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);

                        mLayoutKeyIn.setVisibility(View.VISIBLE);
//                        mBtnNext.setVisibility(View.VISIBLE);

                    }
                }).start();

        Call<ResponseBody> call = services.billService(new RequestModel(APIServices.ACTION_LOAD_BILL_REF,
                new LoadBillRefRequest(mResponse.getBILL_SERVICE_ID())));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (objectResponse instanceof String) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    ArrayList<LoadBillRefResponse> mListBillRef = gson
                            .fromJson((String) objectResponse,
                                    new TypeToken<ArrayList<LoadBillRefResponse>>() {
                                    }.getType());

                    if (mListBillRef != null && mListBillRef.size() > 0){
                        BillRefAdapter adapter = new BillRefAdapter(mListBillRef);

                        switch (MyApplication.getTypeScreenLayout()){
                            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                mRecyclerRef.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                break;
                            default:
                                mRecyclerRef.setLayoutManager(new LinearLayoutManager(getContext()));

                        }
                        mRecyclerRef.setAdapter(new ScaleInAnimationAdapter(adapter));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
            }
        });
    }

    public class BillRefAdapter extends RecyclerView.Adapter<BillRefAdapter.ViewHolder>{
        private ArrayList<LoadBillRefResponse> mListData;

        public BillRefAdapter(ArrayList<LoadBillRefResponse> listdata) {
            this.mListData = listdata;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_bill_reference, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LoadBillRefResponse refResponse = getItem(0);
            holder.mTextTitleRef.setText(refResponse.getLABEL_NAME());
            holder.mEditRef.setHint(refResponse.getLABEL_NAME() + " (" + refResponse.getLENGTHCODE() + " Digit)");

            if (position == getItemCount()-1){
                holder.mEditRef.setImeOptions(EditorInfo.IME_ACTION_DONE);
            } else {
                holder.mEditRef.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
        }

        @Override
        public int getItemCount() {
            return mListData.size()+1;
        }

        public LoadBillRefResponse getItem(int position){
            return mListData.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView mTextTitleRef;
            private EditText mEditRef;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextTitleRef = itemView.findViewById(R.id.text_title_ref);
                mEditRef = itemView.findViewById(R.id.edit_ref);
            }
        }
    }

}

