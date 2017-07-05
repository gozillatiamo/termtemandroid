package com.worldwidewealth.termtem.dashboard.billpayment.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.widgets.MenuButtonView;

/**
 * Created by gozillatiamo on 7/4/17.
 */

public class BillPayMenuAdapter extends RecyclerView.Adapter<BillPayMenuAdapter.ViewHolder> {

    private Context mContext;
    private String[] mStrListMenu;



    public BillPayMenuAdapter(Context context, String[] listmenu) {
        this.mContext = context;
        mStrListMenu = listmenu;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new MenuButtonView(mContext));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuButtonView menuButtonView = (MenuButtonView) holder.itemView;
        menuButtonView.setTitle(mStrListMenu[position]);
        TypedArray typedArrayIcon = mContext.getResources().obtainTypedArray(R.array.list_icon_main_bill);
        menuButtonView.setIcon(typedArrayIcon.getResourceId(position, -1));
        typedArrayIcon.recycle();
    }

    @Override
    public int getItemCount() {
        return mStrListMenu.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
