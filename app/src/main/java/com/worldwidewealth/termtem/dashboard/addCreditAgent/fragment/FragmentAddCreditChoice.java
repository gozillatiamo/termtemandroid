package com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.FragmentTopupPreview;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.adapter.AgentAdapter;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.model.TopupPreviewResponseModel;
import com.worldwidewealth.termtem.widgets.SelectAmountAndOtherFragment;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.AgentResponse;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.GetOTPRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.model.TopupPreviewRequestModel;
import com.worldwidewealth.termtem.model.TopupResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.BottomAction;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddCreditChoice extends Fragment {
    public static final String TAG = FragmentAddCreditChoice.class.getSimpleName();

    private String[] mAmount;
    private ViewHolder mHolder;
    private AgentResponse mAgent;
    private BottomAction mBottomAction;
    private APIServices services;
    private Call<ResponseBody> call;
    private Callback<ResponseBody> callback;
    private double mFavAmt = 0;
    private boolean mIsFavAmt;
    private byte[] imageByte = null;
    private String transid;
    public static final String AGENT_CASHIN = "agentcashin";
    private boolean mIsFav;

    public FragmentAddCreditChoice() {
        // Required empty public constructor
    }

    public static FragmentAddCreditChoice newInstance(Bundle bundle) {
        
        Bundle args = bundle;
        FragmentAddCreditChoice fragment = new FragmentAddCreditChoice();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_credit_choice, container, false);
        mHolder = new ViewHolder(rootView);
        mAgent = getArguments().getParcelable(AgentAdapter.AGENTDATA);
        if (getArguments().containsKey(ActivityTopup.KEY_AMT)){
            mFavAmt = getArguments().getDouble(ActivityTopup.KEY_AMT);
        }
        services = APIServices.retrofit.create(APIServices.class);
        initData();
        initBtn();
        initGrid();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        new DialogCounterAlert.DialogProgress(getContext()).show();

        if (Global.getInstance().hasSubmit() && Global.getInstance().getSubmitStatus()){
            RequestModel requestModel = Global.getInstance().getLastSubmit();
            SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel) requestModel.getData();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_add_credit, FragmentTopupSlip.newInstance(FragmentTopupSlip.PREVIEW,
                            MyApplication.getTypeToup(requestModel.getAction()),
                            submitTopupRequestModel.getTRANID(),
                            Global.getInstance().getSubmitIsFav())).commit();
//            Util.getPreviousEslip(this, mTopup, R.id.container_topup);
        }

/*
        if (Global.getInstance().getAGENTID() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (imageByte != null) {
                        getActivity().finish();
*/
/*
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_add_credit, FragmentTopupSlip.newInstance(imageByte, transid)).commit();
*//*

                    }
                }
            }, 2000);
        }
*/

    }

    private void initData(){
        mAmount = new String[]{
                "100",
                "200",
                "300",
                "400",
                "500",
                "600",
                "700",
                "800",
                "900",
                "1000",
                "2000",
                getString(R.string.other)
        };

        mHolder.mPhoneNo.setText(PhoneNumberUtils.formatNumber(mAgent.getPhoneno()));
        mHolder.mAgentName.setText(mAgent.getFirstName() + "\t" + mAgent.getLastName());
    }

    private void initBtn(){
        mBottomAction = new BottomAction(getContext(),
                mHolder.mIncludeBottom,
                BottomAction.NEXT, new BottomAction.OnActionClickListener() {
            @Override
            public void onActionClick() {
                mBottomAction.setEnable(false);
                if (Double.parseDouble(mBottomAction.getPrice()) == 0){
                    Toast.makeText(getContext(), R.string.please_choice_topup, Toast.LENGTH_LONG).show();
                    mBottomAction.setEnable(true);
                    return;
                }

                if (Double.parseDouble(mBottomAction.getPrice()) < 1 || Double.parseDouble(mBottomAction.getPrice()) > 49000){
                    Toast.makeText(getContext(), getString(R.string.alert_amount_mpay_over), Toast.LENGTH_LONG).show();
                    mBottomAction.setEnable(true);
                    return;
                }

                if (Global.getInstance().getBALANCE() < Double.parseDouble(mBottomAction.getPrice())){
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setMessage(R.string.balance_not_enough)
                            .setPositiveButton(R.string.confirm, null)
                            .show();
                    mBottomAction.setEnable(true);

                    return;
                }


                servicePreview();

            }
        });
    }

    private void servicePreview(){
        new DialogCounterAlert.DialogProgress(getContext()).show();
        call = services.topupService(
                new RequestModel(APIServices.ACTION_PREVIEW_AGENT_CASHIN,
                        new TopupPreviewRequestModel(Double.parseDouble(mBottomAction.getPrice()), null)));
        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (objectResponse instanceof String) {
                    SelectAmountAndOtherFragment fragmentAmount = (SelectAmountAndOtherFragment) getChildFragmentManager().findFragmentById(R.id.container_select_amount);
                    mIsFav = fragmentAmount.getIsFav();
                    TopupPreviewResponseModel model = new Gson().fromJson((String) objectResponse, TopupPreviewResponseModel.class);
                    FragmentTransaction transaction = getChildFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right)
                            .addToBackStack(null)
                            .replace(R.id.container_select_amount, FragmentTopupPreview.newInstance(AGENT_CASHIN, model, mBottomAction.getPrice()));
                    transaction.commit();


                    mBottomAction.swichType(BottomAction.SUBMIT, new BottomAction.OnActionClickListener() {
                        @Override
                        public void onActionClick() {
                            mBottomAction.setEnable(false);
                            if(getFragmentManager().findFragmentById(R.id.container_topup_package) instanceof FragmentTopupPreview){
                                FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview) getFragmentManager().findFragmentById(R.id.container_topup_package);
                                if (!fragmentTopupPreview.canTopup()) {
                                    mBottomAction.setEnable(true);
                                    return;
                                }
                            }

                            serviceTopup();
                        }
                    });
                }

                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
                mBottomAction.setEnable(true);


            }
        });
    }

    private void serviceTopup(){
        new DialogCounterAlert.DialogProgress(getContext()).show();
        call = services.topupService(
                new RequestModel(APIServices.ACTION_GETOTP_AGENT_CASHIN, new GetOTPRequestModel()));
        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (responseValues == null){
                    mBottomAction.setEnable(true);
                    return;
                }

                if (responseValues instanceof String){
                    serviceSubmitToup((String)responseValues);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
                mBottomAction.setEnable(true);
            }
        });

    }

    private void serviceSubmitToup(final String responseStr){

        final TopupResponseModel model = new Gson().fromJson(responseStr, TopupResponseModel.class);
        this.transid  = model.getTranid();

        RequestModel requestModel = new RequestModel(APIServices.ACTION_SUBMIT_AGENT_CASHIN,
                SubmitTopupRequestModel.SubmitAgentRequestModel(mBottomAction.getPrice(),
                        mAgent.getPhoneno(),
                        transid,
                        mAgent.getAgentId()));

        Global.getInstance().setLastSubmit(requestModel, mIsFav);
        Global.getInstance().setSubmitStatus(null);

        call = services.submitTopup(requestModel);

        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (responseValues == null) {
                    mBottomAction.setEnable(true);
                    return;
                }

                if (responseValues instanceof ResponseModel){
//                    serviceEslip(model.getTranid());
                    if (MyApplication.LeavingOrEntering.currentActivity instanceof ActivityAddCreditAgent) {
                        try {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_add_credit, FragmentTopupSlip.newInstance(FragmentTopupSlip.PREVIEW, AGENT_CASHIN, transid, mIsFav)).commit();
                        } catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), null, call, this, false);
                mBottomAction.setEnable(true);
            }
        });
    }


/*
    private void serviceEslip(final String transid){
        if (!(MyApplication.LeavingOrEntering.currentActivity instanceof ActivityAddCreditAgent)) return;
        call = services.eslip(new RequestModel(APIServices.ACTION_ESLIP_AGENT_CASHIN, new EslipRequestModel(transid, mAgent.getPhoneno())));
        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (responseValues == null) return;

                if (responseValues instanceof ResponseModel){
                    imageByte = Base64.decode(((ResponseModel)responseValues).getFf()
                            , Base64.NO_WRAP);
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    try {

                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_add_credit, FragmentTopupSlip.newInstance(imageByte, transid, mIsFav)).commit();
                    } catch (IllegalStateException e){e.printStackTrace();}
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), null, call, this, false);
                mBottomAction.setEnable(true);
            }
        });
    }
*/


    private void initGrid(){
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.container_select_amount, SelectAmountAndOtherFragment.newInstance(mBottomAction, mAmount, mFavAmt))
                .commit();
    }


    private class ViewHolder{
        private View mIncludeBottom;
        private TextView mPhoneNo, mAgentName;
        public ViewHolder(View itemView) {
            mIncludeBottom = (View) itemView.findViewById(R.id.include_bottom_action);
            mPhoneNo = (TextView) itemView.findViewById(R.id.edit_phone);
            mAgentName = (TextView) itemView.findViewById(R.id.txt_agent_name);
        }
    }

}
