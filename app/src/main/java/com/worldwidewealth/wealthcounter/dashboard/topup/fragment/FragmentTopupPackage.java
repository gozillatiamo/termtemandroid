package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.adapter.AdapterPageTopup;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPackage extends  Fragment{
    private View rootView;
    private ViewHolder mHolder;
    private String mTopupService;
    private APIServices services;

    private static final String TOPUPSERVICE = "service";
    public static Fragment newInstance(String service){
        FragmentTopupPackage fragment = new FragmentTopupPackage();
        Bundle bundle = new Bundle();
        bundle.putString(TOPUPSERVICE, service);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTopupService = getArguments().getString(TOPUPSERVICE);
        services = APIServices.retrofit.create(APIServices.class);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_package, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

//        mHolder.mViewPage.setAdapter(new AdapterPageTopup(getChildFragmentManager()));
//        mHolder.mTab.setupWithViewPager(mHolder.mViewPage);
        initPageTopup();
        initData();
        initBtn();
        return rootView;
    }

    private void initPageTopup(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container_topup_package, FragmentAirtimeVAS.newInstance())
                .commit();
    }

    private void initData(){
        switch (mTopupService){
            case Configs.TopupServices.AIS:
                mHolder.mLogoService.setImageResource(R.drawable.ais_logo);
                break;
            case Configs.TopupServices.TRUEMOVE:
                mHolder.mLogoService.setImageResource(R.drawable.truemove_logo);
                break;
            case Configs.TopupServices.DTAC:
                mHolder.mLogoService.setImageResource(R.drawable.dtac_logo);
                break;
        }
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.mBtnNext.setVisibility(View.GONE);
                mHolder.mBtnTopup.setVisibility(View.VISIBLE);
                FragmentTopupPackage.this.getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container_topup_package, FragmentTopupPreview.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mHolder.mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = null;

                switch (Global.getPage()){
                    case 0:
                        call = services.online();
                        break;
                    case 1:
                        call = services.pin();
                        break;
                }

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("response", response.raw().toString());
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Call<ResponseBody> callLuck = services.getluck();
                                callLuck.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Log.e("getLuck", response.raw().toString());
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                                .replace(R.id.container_topup, FragmentTopupSlip.newInstance())
                                                .addToBackStack(null)
                                                .commit();

                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });

                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

            }
        });

    }

    public class ViewHolder{
        private ViewPager mViewPage;
        private TabLayout mTab;
        private Button mBtnNext, mBtnTopup;
        private ImageView mLogoService;
        public ViewHolder(View itemview){
//            mViewPage = (ViewPager) itemview.findViewById(R.id.viewpager);
//            mTab = (TabLayout) itemview.findViewById(R.id.tab_package);
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTopup = (Button) itemview.findViewById(R.id.btn_topup);
            mLogoService = (ImageView) itemview.findViewById(R.id.logo_service);

        }
    }

}
