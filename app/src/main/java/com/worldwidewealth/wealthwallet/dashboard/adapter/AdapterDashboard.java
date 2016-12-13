package com.worldwidewealth.wealthwallet.dashboard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentTopup;

/**
 * Created by MyNet on 7/11/2559.
 */

public class AdapterDashboard extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public static final int TYPE_2COLUMN = 6;
    public static final int TYPE_3COLUMN = 4;
    public static final int TYPE_4COLUMN = 3;
    public static final int TOPUP = 6;
    private String[] mTitleMenu;
    private TypedArray mIconMenu;
    private int[] mColorMenu;

    public AdapterDashboard(Context context){
        this.mContext = context;
        this.mTitleMenu = mContext.getResources().getStringArray(R.array.str_list_dashboard);
        this.mIconMenu = mContext.getResources().obtainTypedArray(R.array.ic_list_dashboard);
        this.mColorMenu = mContext.getResources().getIntArray(R.array.color_list_dashboard);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_dashboard_vertical, parent, false);
        return new VerticalHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        VerticalHolder verticalHolder = (VerticalHolder)holder;

        Drawable drawable = mContext.getResources().getDrawable(mIconMenu.getResourceId(position, -1));
        verticalHolder.mIcon.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        verticalHolder.mBackgroundMenu.setBackgroundColor(mColorMenu[position]);

        if (position == 11 || position == 12){
            verticalHolder.itemView.setPadding(0, 80, 0, 80);
            verticalHolder.mTextTitle.setPadding(0, 20, 0, 0);
        }
        if (position == 2 || position == 3){
            verticalHolder.mIcon.setText(mTitleMenu[position]);
            verticalHolder.mIcon.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelOffset(R.dimen.activity_space));
            verticalHolder.mTextTitle.setText("100,000");
            verticalHolder.mTextTitle.setTextSize(24);
            verticalHolder.mTextCurrency.setVisibility(View.VISIBLE);
        }else {
            verticalHolder.mTextTitle.setSingleLine();
            verticalHolder.mTextTitle.setText(mTitleMenu[position]);
        }

        initBtn(holder, position);
    }

    private void initBtn(RecyclerView.ViewHolder holder, final int position){

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (position){
                    case TOPUP:
                        startFragment(FragmentTopup.newInstance());
                        break;
                }

            }
        });
    }

    private void startFragment(Fragment fragment){

//        FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager()
//                .beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
//                .replace(R.id.dashboard_container, fragment)
//                .addToBackStack(null);
//        transaction.commit();

    }


    @Override
    public int getItemCount() {
        return mTitleMenu.length;
    }

    @Override
    public int getItemViewType(int position) {

        if (position >= 4 && position <= 6 ){
            return TYPE_3COLUMN;
        } else if (position >= 7 && position <= 10){
            return TYPE_4COLUMN;
        }
        return TYPE_2COLUMN;
    }

    public class VerticalHolder extends RecyclerView.ViewHolder{

        private TextView mIcon, mTextTitle, mTextCurrency;
        private View mBackgroundMenu;
        public VerticalHolder(View itemView) {
            super(itemView);
            mIcon = (TextView) itemView.findViewById(R.id.icon);
            mTextTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mBackgroundMenu = (View) itemView.findViewById(R.id.menu_dashboard);
            mTextCurrency = (TextView) itemView.findViewById(R.id.txt_currency);
        }
    }

    public class HorizontalHolder extends RecyclerView.ViewHolder{

        public HorizontalHolder(View itemView) {
            super(itemView);
        }
    }
}
