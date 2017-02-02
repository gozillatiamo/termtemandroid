package com.worldwidewealth.termtem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by MyNet on 11/10/2559.
 */

public class ActivityShowNotify extends AppCompatActivity {
    private String mStrTitle, mStrBox;
    private ViewHolder mHolder;
    public static final String TAG = ActivityShowNotify.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mStrTitle = bundle.getString(MyFirebaseMessagingService.TEXT);
            mStrBox = bundle.getString(MyFirebaseMessagingService.BOX);
        }
        setContentView(R.layout.activity_show_notify);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        mHolder = new ViewHolder(this);

        mHolder.mTextTitle.setText(mStrTitle);
        mHolder.mTextBox.setText(mStrBox);
        Log.e(TAG, "onCreate");
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mStrTitle = bundle.getString(MyFirebaseMessagingService.TEXT);
            mStrBox = bundle.getString(MyFirebaseMessagingService.BOX);
            Log.e(TAG, "txt: "+ mStrTitle +"\nbox: "+mStrBox);

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            mStrTitle = bundle.getString(MyFirebaseMessagingService.TEXT);
            mStrBox = bundle.getString(MyFirebaseMessagingService.BOX);
            Log.e(TAG, "txt: "+ mStrTitle +"\nbox: "+mStrBox);
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