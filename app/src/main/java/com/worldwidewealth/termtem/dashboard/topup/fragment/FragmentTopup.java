package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ServiceProRequestModel;
import com.worldwidewealth.termtem.model.ServiceProResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.TermTemLoading;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopup extends Fragment implements View.OnClickListener{
    private View rootView;
    private ViewHolder mHolder;
    private String mTopup;
    private APIServices services = APIServices.retrofit.create(APIServices.class);
    private TermTemLoading loading;
    private boolean isClickable = true;

    public static final String keyTopup = "topup";
    public static final String MOBILE = "mobile";
    public static final String PIN = "pin";
    public static final String VAS = "vas";


    public static Fragment newInstance(String topup){
        Bundle bundle = new Bundle();
        FragmentTopup fragment = new FragmentTopup();
        bundle.putString(keyTopup, topup);
        fragment.setArguments(bundle);
        return fragment;
    }


    public class ViewHolder{
        private CardView mBtnAis, mBtnTruemove, mBtnDtac;
        private View mIncludeMyWallet;
        private ImageView mImageTrue, mImageAis;

        public ViewHolder(View itemview){
            mBtnAis = (CardView) itemview.findViewById(R.id.btn_ais);
            mBtnTruemove = (CardView) itemview.findViewById(R.id.btn_truemove);
            mBtnDtac = (CardView) itemview.findViewById(R.id.btn_dtac);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);
            mImageTrue = (ImageView) itemview.findViewById(R.id.img_true);
            mImageAis = (ImageView) itemview.findViewById(R.id.img_ais);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mTopup = this.getArguments().getString(keyTopup);
        initBtnServices();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        isClickable = true;

    }

    private void initBtnServices(){
        if (loading == null){
            loading = new TermTemLoading(getContext(), (ViewGroup) getActivity().findViewById(R.id.activity_topup));
        }

        loading.show();
        ServiceProRequestModel servicePro = null;
        switch (mTopup){
            case MOBILE:
                servicePro = new ServiceProRequestModel(ServiceProRequestModel.SCODE_TOPUP);
                break;
            case PIN:
                servicePro = new ServiceProRequestModel(ServiceProRequestModel.SCODE_EPIN);
                mHolder.mImageAis.setImageResource(R.drawable.logo_ais_pin);
                mHolder.mImageTrue.setImageResource(R.drawable.logo_truemoney);
                mHolder.mBtnDtac.setVisibility(View.GONE);
                break;
            case VAS:
                mHolder.mBtnAis.setVisibility(View.VISIBLE);
                loading.hide();
                break;
        }

        if (servicePro != null) {
            Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTION_SERVICE_PRO, servicePro));
            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                    if (responseValues instanceof String){
                        List<ServiceProResponseModel> models = new Gson()
                                .fromJson((String)responseValues,
                                        new TypeToken<List<ServiceProResponseModel>>(){}.getType());
                        for (ServiceProResponseModel model : models) {
                            switch (model.getCARRIERCODE()) {
                                case APIServices.AIS:
                                    mHolder.mBtnAis.setVisibility(View.VISIBLE);
                                    break;
                                case APIServices.TRUEMOVE:
                                    mHolder.mBtnTruemove.setVisibility(View.VISIBLE);
                                    break;
                                case APIServices.DTAC:
                                    mHolder.mBtnDtac.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
/*
                    else if (responseValues == null && (getString(R.string.server).contains("test") ||
                            getString(R.string.server).contains("203.154.162.119"))){
                        mHolder.mBtnAis.setVisibility(View.VISIBLE);
                        mHolder.mBtnTruemove.setVisibility(View.VISIBLE);
                        mHolder.mBtnDtac.setVisibility(View.VISIBLE);
                    }
*/

                    loading.hide();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loading.hide();
                    new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
                }
            });
        }


        mHolder.mBtnAis.setOnClickListener(this);

        mHolder.mBtnTruemove.setOnClickListener(this);

        mHolder.mBtnDtac.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (!isClickable) return;

        isClickable = false;
        String service = null;

        switch (v.getId()) {

            case R.id.btn_ais:
                service = APIServices.AIS;
                break;
            case R.id.btn_truemove:
                service = APIServices.TRUEMOVE;

                break;
            case R.id.btn_dtac:
                service = APIServices.DTAC;
                break;
        }

        if (service != null) {
            startFragmentService(service);
        }


    }


    private void initData(){
//        Util.setBalanceWallet(mHolder.mIncludeMyWallet);
        Util.updateMyBalanceWallet(getContext(), mHolder.mIncludeMyWallet);
    }

    private void startFragmentService(String service){

        this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .replace(R.id.container_topup, FragmentTopupPackage.newInstance(service, mTopup, null))
                .addToBackStack(null)
                .commit();
    }

}
