package com.worldwidewealth.wealthcounter.dashboard.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillPayment;
import com.worldwidewealth.wealthcounter.dashboard.moneytransfer.fragment.FragmentMT;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentTopup;

/**
 * Created by gozillatiamo on 10/4/16.
 */
public class AdapterListDashboard extends RecyclerView.Adapter<AdapterListDashboard.ViewHolder> {

    private Context mContext;
    private String[] mTextList;
    private TypedArray mIconList;
    private int[] mColorList;
    private static final int BP = 0;
    private static final int MT = 1;
    private static final int TO = 2;
    private static final int TP = 3;

    public AdapterListDashboard(Context context) {

        this.mContext = context;
        mTextList = mContext.getResources().getStringArray(R.array.str_list_dashboard);
        mIconList = mContext.getResources().obtainTypedArray(R.array.ic_list_dashboard);
        mColorList = mContext.getResources().getIntArray(R.array.color_list_dashboard);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.column_list_dashboard, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        if (pos != 2){

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (pos){
                    case BP:
                        //startFragment(FragmentBillPayment.newInstance());
                        break;
                    case MT:
                       // startFragment(FragmentMT.newInstance());
                        break;
                    default:
                        if (pos == TO) Global.setPage(0);
                        else if (pos == TP) Global.setPage(1);
                        startFragment(FragmentTopup.newInstance());
                        break;
                }



            }
        });

        holder.mIcon.setImageResource(mIconList.getResourceId(position, -1));
        holder.mIcon.setColorFilter(mColorList[position]);

        holder.mTitle.setText(mTextList[position]);
        holder.mTitle.setTextColor(mColorList[position]);

        holder.mCoins.setTextColor(mColorList[position]);
    }

    private void startFragment(Fragment fragment){

        FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.dashboard_container, fragment)
                .addToBackStack(null);
        transaction.commit();

    }

    @Override
    public int getItemCount() {
        return mTextList.length-1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIcon;
        private TextView mTitle, mCoins;

        public ViewHolder(View itemView) {
            super(itemView);

            mIcon = (ImageView) itemView.findViewById(R.id.ic_list);
            mTitle = (TextView) itemView.findViewById(R.id.title_list);
            mCoins = (TextView) itemView.findViewById(R.id.text_coins);
        }
    }
}
