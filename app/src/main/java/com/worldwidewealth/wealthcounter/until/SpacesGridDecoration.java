package com.worldwidewealth.wealthcounter.until;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by MyNet on 7/11/2559.
 */

public class SpacesGridDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public SpacesGridDecoration (int space){
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = mSpace;
        outRect.left = mSpace;
    }
}
