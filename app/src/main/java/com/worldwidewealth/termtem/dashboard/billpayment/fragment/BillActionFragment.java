package com.worldwidewealth.termtem.dashboard.billpayment.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.worldwidewealth.termtem.dashboard.favorite.FavoritesActivity;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.LoadBillRefRequest;
import com.worldwidewealth.termtem.model.LoadBillRefResponse;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.CheckSyntaxData;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private View mLayoutKeyIn, mLayoutEnterPhone;
    private RecyclerView mRecyclerRef;
    private APIServices services;
    private String mPhoneNo;
    private BillRefAdapter mRefAdapter;

    public static final String TAG = BillActionFragment.class.getSimpleName();

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
        Util.setupUI(getView());
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
                    Log.e(TAG, "result OnActivityResult: "+result);

                    Intent intent = new Intent(getContext(), ActivityTopup.class);
                    intent.putExtra(FragmentTopup.keyTopup, BillPaymentActivity.BILLPAY);
                    intent.putExtra(ActivityTopup.KEY_PHONENO, mPhoneNo);
                    intent.putExtra(FragmentTopupPackage.KEY_BARCODE, result);
                    intent.putExtra(ActivityTopup.KEY_BILLSERVICE, response);

                    startActivity(intent);
                    getActivity().finish();


/*
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
*/

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
        mLayoutEnterPhone = getView().findViewById(R.id.layout_enter_phone);
        if (mResponse.getBILL_SERVICE_CODE().equals(BillPaymentActivity.AIS_PHONE_CODE)){
            mLayoutEnterPhone.setVisibility(View.GONE);
        }

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
        mBtnNext.setOnClickListener(this);
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
        else mBtnScan.setVisibility(View.VISIBLE);
        if (mResponse.getKEYIN() == 0) mBtnKeyIn.setVisibility(View.GONE);
        else mBtnKeyIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (!mResponse.getBILL_SERVICE_CODE().equals(BillPaymentActivity.AIS_PHONE_CODE)) {
            mPhoneNo = mEditPhoneNo.getText().toString().replaceAll("-", "");
            if (!CheckSyntaxData.isPhoneValid(mPhoneNo)) {
                Toast.makeText(getContext(), R.string.hint_phone_number, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        switch (view.getId()){
            case R.id.btn_scan:
//                MyApplication.LeavingOrEntering.currentActivity = null;
                if (mResponse.getBILL_SERVICE_CODE().equals(BillPaymentActivity.PEA_CODE)){
                    initExampleDialog();
                } else {
                    Intent intent = new Intent(getContext(), ScanBillActivity.class);
                    intent.putExtra(MainBillPayFragment.KEY_BILL_DATA, mResponse);
                    startActivityForResult(intent,
                            BillPaymentActivity.SCAN);
                }

                break;
            case R.id.btn_key_in:
                mBtnKeyIn.animate()
                        .setDuration(500)
                        .alpha(0f).start();
                mLogoService.animate()
                        .setDuration(500)
                        .alpha(0.2f).start();
                mBtnScan.setVisibility(View.GONE);
                setupRecycler();
                break;
            case R.id.btn_next:

                String allRef = null;
                for (int i = 0; i < mRefAdapter.getItemCount(); i++){
                    BillRefAdapter.ViewHolder viewHolder = (BillRefAdapter.ViewHolder) mRecyclerRef.findViewHolderForAdapterPosition(i);
                    String ref = viewHolder.mEditRef.getText().toString();
                    if (ref == null || ref.isEmpty()){
                        Toast.makeText(getContext(), getString(R.string.please_enter_bill_ref), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    switch (i){
                        case 0:
                            allRef = ref;
                            break;
                        default: allRef += ":"+ref;
                    }
                }

                if (mResponse.getBILL_SERVICE_CODE().equals(BillPaymentActivity.AIS_PHONE_CODE)){
                    mPhoneNo = allRef;
                }

                Intent intent = new Intent(getContext(), ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, BillPaymentActivity.BILLPAY);
                intent.putExtra(ActivityTopup.KEY_PHONENO, mPhoneNo);
                intent.putExtra(FragmentTopupPackage.KEY_BILLREF, allRef);
                intent.putExtra(ActivityTopup.KEY_BILLSERVICE, mResponse);

                startActivity(intent);
                getActivity().finish();

                break;
        }
    }

    private void initExampleDialog(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_duedate, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false).show();

        ImageView image_example = dialogView.findViewById(R.id.image_example);
        image_example.setImageResource(R.drawable.guide_bill);
        Button btn_confirm = dialogView.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                Intent intent = new Intent(getContext(), ScanBillActivity.class);
                intent.putExtra(MainBillPayFragment.KEY_BILL_DATA, mResponse);
                startActivityForResult(intent,
                        BillPaymentActivity.SCAN);

            }
        });
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
                        mRefAdapter = new BillRefAdapter(mListBillRef);

                        switch (MyApplication.getTypeScreenLayout()){
                            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                mRecyclerRef.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                break;
                            default:
                                mRecyclerRef.setLayoutManager(new LinearLayoutManager(getContext()));

                        }
                        mRecyclerRef.setAdapter(new ScaleInAnimationAdapter(mRefAdapter));
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
            return mListData.size();
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

