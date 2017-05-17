package com.worldwidewealth.termtem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;

import com.worldwidewealth.termtem.model.AttachResponseModel;
import com.worldwidewealth.termtem.model.FileListNotifyResponseModel;
import com.worldwidewealth.termtem.model.ReadMsgRequest;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.widgets.InformationView;
import com.worldwidewealth.termtem.widgets.WidgetTypeInbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private int mType;
    private ArrayList<FileListNotifyResponseModel> mFileList;

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
        MyApplication.LeavingOrEntering.currentActivity = null;

        if (bundle != null) {
            mStrTitle = bundle.getString(MyFirebaseMessagingService.TEXT);
            mStrBox = bundle.getString(MyFirebaseMessagingService.BOX);
            mMsgid = bundle.getString(MyFirebaseMessagingService.MSGID);
            if (bundle.containsKey(MyFirebaseMessagingService.FILELIST)){
                mFileList = bundle.getParcelableArrayList(MyFirebaseMessagingService.FILELIST);
            }

            if (bundle.containsKey(MyFirebaseMessagingService.TYPE)){
                mType = bundle.getInt(MyFirebaseMessagingService.TYPE);
                mHolder.mTypeInbox.setWidgetType(mType);
                switch (InformationView.TYPE.getTypeAt(mType)){
                    case IMAGE:
                        setImage();
                        break;
                }


            }

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

            Linkify.TransformFilter filter = new Linkify.TransformFilter() {
                public final String transformUrl(final Matcher match, String url) {
                    return match.group();
                }
            };

            Pattern pattern = Pattern.compile("(\\d{9,13})");
            Linkify.addLinks(mHolder.mTextBox, pattern, "tel:", null, filter);

            Pattern urlPattern = Patterns.WEB_URL;
            Linkify.addLinks(mHolder.mTextBox, urlPattern, "http://", null, filter);

            Pattern urlsPattern = Patterns.WEB_URL;
            Linkify.addLinks(mHolder.mTextBox, urlsPattern, "https://", null, filter);

        }

    }

    private void setImage(){
        if (mFileList == null) return;
        List<AttachResponseModel> listImage = new ArrayList<>();
        for (FileListNotifyResponseModel model : mFileList){
            AttachResponseModel attach = new AttachResponseModel();
            attach.setCONTENTTYPE(model.getContentype());
            attach.setFILETYPE(model.getFiletype());
            attach.setFILESIZE(model.getSize());
            attach.setURLFILE(model.getUrl());

            listImage.add(attach);
        }

        mHolder.mTypeInbox.setImage(listImage);
    }

    private class ViewHolder{

        private TextView mTextTitle, mTextBox;
        private WidgetTypeInbox mTypeInbox;

        public ViewHolder(Activity itemView){
            mTextTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mTextBox = (TextView) itemView.findViewById(R.id.txt_box);
            mTypeInbox = (WidgetTypeInbox) itemView.findViewById(R.id.widget_type);
        }
    }
}
