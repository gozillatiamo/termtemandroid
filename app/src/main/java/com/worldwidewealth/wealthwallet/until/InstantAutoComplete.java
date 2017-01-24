package com.worldwidewealth.wealthwallet.until;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by user on 11-Jan-17.
 */

public class InstantAutoComplete extends AutoCompleteTextView {

    public static final String TAG = InstantAutoComplete.class.getSimpleName();

    public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantAutoComplete(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            try {
                performFiltering(getText(), 0);
            } catch (NullPointerException e){}
        }
    }

}