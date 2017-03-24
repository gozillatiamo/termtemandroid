package com.worldwidewealth.termtem.widgets;

import android.util.SparseBooleanArray;

/**
 * Created by user on 24-Mar-17.
 */

public class MultiSelector {
    private SparseBooleanArray mSelectedPositions;
    private boolean mIsSelectable;

    public MultiSelector(){
        mSelectedPositions = new SparseBooleanArray();
        mIsSelectable = true;
    }

    public void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    public boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    public void setSelectable(boolean selectable) {
        mIsSelectable = selectable;
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

}
