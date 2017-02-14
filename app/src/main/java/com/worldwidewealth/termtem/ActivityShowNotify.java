package com.worldwidewealth.termtem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.worldwidewealth.termtem.model.ReadMsgRequest;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by MyNet on 11/10/2559.
 */

public class ActivityShowNotify extends MyAppcompatActivity {
    private String mStrTitle, mStrBox, mMsgid;
    private ViewHolder mHolder;
    private APIServices services;
    public static final String TAG = ActivityShowNotify.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_show_notify);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        mHolder = new ViewHolder(this);
        services = APIServices.retrofit.create(APIServices.class);
        initData(bundle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");
        Bundle bundle = intent.getExtras();
        initData(bundle);
    }

    private void initData(Bundle bundle){
        if (bundle != null) {
            MyApplication.LeavingOrEntering.currentActivity = null;
            mStrTitle = bundle.getString(MyFirebaseMessagingService.TEXT);
            mStrBox = bundle.getString(MyFirebaseMessagingService.BOX);
            mMsgid = bundle.getString(MyFirebaseMessagingService.MSGID);
            if (mMsgid != null && !mMsgid.equals("")) {
                Call<ResponseBody> call = services.service(
                        new RequestModel(APIServices.ACTIONREADMSG,
                                new ReadMsgRequest(mMsgid)));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
            Log.e(TAG, "txt: " + mStrTitle + "\nbox: " + mStrBox + "\nmsgid: " + mMsgid);
            mHolder.mTextTitle.setText(mStrTitle);
            mHolder.mTextBox.setText(mStrBox);
        }

    }

    private class ViewHolder{

        private TextView mTextTitle, mTextBox;

        public ViewHolder(Activity itemView){
            mTextTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mTextBox = (TextView) itemView.findViewById(R.id.txt_box);
        }
    }
}
