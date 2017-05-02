package com.worldwidewealth.termtem.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.worldwidewealth.termtem.R;

/**
 * Created by user on 02-May-17.
 */

public class MyTextView extends AppCompatTextView{

    public MyTextView(Context context) {
        super(context);
        setup(null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs){
        setupStyleable(attrs);
    }

    private void setupStyleable(AttributeSet attrs){
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);

            Drawable drawableLeft = null;
            Drawable drawableRight = null;
            Drawable drawableTop = null;
            Drawable drawableBottom = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                drawableLeft = typedArray.getDrawable(R.styleable.MyTextView_drawableLeftCompat);
                drawableRight = typedArray.getDrawable(R.styleable.MyTextView_drawableRightCompat);
                drawableTop = typedArray.getDrawable(R.styleable.MyTextView_drawableTopCompat);
                drawableBottom = typedArray.getDrawable(R.styleable.MyTextView_drawableBottomCompat);
            } else {
                final int drawableLeftId = typedArray.getResourceId(R.styleable.MyTextView_drawableLeftCompat, -1);
                final int drawableRightId = typedArray.getResourceId(R.styleable.MyTextView_drawableRightCompat, -1);
                final int drawableTopId = typedArray.getResourceId(R.styleable.MyTextView_drawableTopCompat, -1);
                final int drawableBottomId = typedArray.getResourceId(R.styleable.MyTextView_drawableBottomCompat, -1);

                if (drawableLeftId != -1)
                    drawableLeft = AppCompatResources.getDrawable(getContext(), drawableLeftId);
                if (drawableRightId != -1)
                    drawableRight = AppCompatResources.getDrawable(getContext(), drawableRightId);
                if (drawableTopId != -1)
                    drawableTop = AppCompatResources.getDrawable(getContext(), drawableTopId);
                if (drawableBottomId != -1)
                    drawableBottom = AppCompatResources.getDrawable(getContext(), drawableBottomId);
            }

            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            typedArray.recycle();

        }
    }
}
