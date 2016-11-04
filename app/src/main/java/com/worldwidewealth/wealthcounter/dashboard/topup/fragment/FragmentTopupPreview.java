package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.APIservices;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private APIservices service;
    private String mPage;
    public static Fragment newInstance(String page){
        Bundle bundle = new Bundle();
        bundle.putString("page", page);
        FragmentTopupPreview fragment = new FragmentTopupPreview();
        fragment.setArguments(bundle);

        return fragment;
    }

    public class ViewHolder{
        private Button mBtnNext;
        private Spinner mListCurrency;
        private TextView mTitle;

        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mListCurrency = (Spinner) itemview.findViewById(R.id.spinner);
            mTitle  =(TextView) itemview.findViewById(R.id.title);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPage = getArguments().getString("page");
        service = APIservices.retrofit.create(APIservices.class);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_preview, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        initSpinnerCurrency();
        switch (mPage){
            case "airtime":
                mHolder.mTitle.setText("เติมเงิน AIS 100 THB");
                break;
            case "vas":
                mHolder.mTitle.setText("เติมเงิน VAS 3GB/100 THB/30 day");
                break;
        }

        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = null;

                switch (Global.getPage()){
                    case 0:
                        call = service.online();
                        break;
                    case 1:
                        call = service.pin();
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
                                Call<ResponseBody> callLuck = service.getluck();
                                callLuck.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Log.e("getLuck", response.raw().toString());
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
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, FragmentTopupSlip.newInstance(mPage))
                        .addToBackStack(null);
                transaction.commit();

            }
        });

        return rootView;
    }

    private void initSpinnerCurrency(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mListCurrency.setAdapter(adapter);
        mHolder.mListCurrency.setSelection(0);

    }


}
