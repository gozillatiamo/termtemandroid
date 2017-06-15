package com.worldwidewealth.termtem.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;

/**
 * Created by user on 17-Apr-17.
 */

public class TermTemLoading extends RelativeLayout {
    private ViewGroup mRootView;
    public static final String TAG = TermTemLoading.class.getSimpleName();
    public TermTemLoading(Context context, ViewGroup rootview) {
        super(context);
        this.mRootView = rootview;
        setupView();
    }

    public TermTemLoading(Context context, AttributeSet attrs, ViewGroup rootview) {
        super(context, attrs);
        this.mRootView = rootview;
        setupView();

    }

    public TermTemLoading(Context context, AttributeSet attrs, int defStyleAttr, ViewGroup rootview) {
        super(context, attrs, defStyleAttr);
        this.mRootView = rootview;
        setupView();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TermTemLoading(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, ViewGroup rootview) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mRootView = rootview;
        setupView();

    }

    private void setupView(){
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setBackgroundColor(Color.parseColor("#50000000"));
        this.setOnClickListener(null);
        AVLoadingIndicatorView loadingIndicatorView = new AVLoadingIndicatorView(getContext());
        loadingIndicatorView.setIndicator(new LineSpinFadeLoaderIndicator());
        int size = getResources().getDimensionPixelSize(R.dimen.icon_large_size);
        loadingIndicatorView.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        loadingIndicatorView.show();
        this.addView(loadingIndicatorView);

        LayoutParams layoutParams = (LayoutParams) loadingIndicatorView.getLayoutParams();
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        loadingIndicatorView.setLayoutParams(layoutParams);

        this.setVisibility(GONE);
        mRootView.addView(this);

    }

    public void show(){
        if (this.isShown()) return;
        TermTemLoading.this.setAlpha(0.0f);
        TermTemLoading.this.setVisibility(VISIBLE);

        this.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }
                });
    }

    public void hide(){
        DialogCounterAlert.DialogProgress.dismiss();
        if (!(this.getVisibility() == VISIBLE)){
            return;
        }
        this.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        TermTemLoading.this.setVisibility(GONE);
                    }
                });
    }
}
