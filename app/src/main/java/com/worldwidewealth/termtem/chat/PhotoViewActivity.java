package com.worldwidewealth.termtem.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;

/**
 * Created by Santipong on 5/30/2017.
 */

public class PhotoViewActivity extends MyAppcompatActivity{

    public static final String EXTRA_RES_ID = "resId";
    public static final String EXTRA_IMG_URL = "resId";

    public static Intent create(final Context context, int resId) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_RES_ID, resId);
        return intent;
    }

    public static Intent create(final Context context, String url) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_IMG_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.photo_view_activity);
        ImageView mImage = (ImageView) findViewById(R.id.ivImage);
        mImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        int resId = getIntent().getIntExtra(EXTRA_RES_ID, 0);
        if (resId == 0){
            Glide.with(this).load(getIntent().getStringExtra(EXTRA_IMG_URL)).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).into(mImage);
        }else {
            Glide.with(this).load(resId).into(mImage);
        }
    }

}
