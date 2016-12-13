package com.worldwidewealth.wealthwallet;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by MyNet on 11/10/2559.
 */

public class ActivityShowNotify extends AppCompatActivity {
    private String mStrTitle, mStrBox;
    private ViewHolder mHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mStrTitle = bundle.getString(MyFirebaseMessagingService.TEXT);
            mStrBox = bundle.getString(MyFirebaseMessagingService.BOX);
            Log.e("Box", mStrBox);
        }
        setContentView(R.layout.activity_show_notify);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        mHolder = new ViewHolder(this);

        mHolder.mTextTitle.setText(mStrTitle);
        mHolder.mTextBox.setText(mStrBox);
//        Log.e("message", getIntent().getExtras().getString("message"));
//        TextView txt_box = (TextView) findViewById(R.id.txt_box);
//        txt_box.setText(this.getIntent().getExtras().getString("message"));
    }

    private class ViewHolder{

        private TextView mTextTitle, mTextBox;

        public ViewHolder(Activity itemView){
            mTextTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mTextBox = (TextView) itemView.findViewById(R.id.txt_box);
        }
    }
}
