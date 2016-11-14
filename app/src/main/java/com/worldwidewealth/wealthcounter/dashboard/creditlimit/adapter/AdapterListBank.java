package com.worldwidewealth.wealthcounter.dashboard.creditlimit.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment.FragmentTransfer;


/**
 * Created by MyNet on 6/10/2559.
 */

public class AdapterListBank extends RecyclerView.Adapter<AdapterListBank.ViewHolder> {
    private Context mContext;
    private String[] mStrBank;
    private TypedArray mIconBank;
    private int[] mColorBank;

    public AdapterListBank(Context context){
        this.mContext = context;

        mStrBank = mContext.getResources().getStringArray(R.array.str_list_bank);
        mIconBank = mContext.getResources().obtainTypedArray(R.array.ic_list_bank);
        mColorBank = mContext.getResources().getIntArray(R.array.color_list_bank);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.colume_list_bank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mIconBank.setImageResource(mIconBank.getResourceId(position, -1));
//        holder.mItemBank.setCardBackgroundColor(mColorBank[position]);
        holder.mNameBank.setText(mStrBank[position]);
        holder.mNameBank.setTextColor(mColorBank[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, new FragmentTransfer().newInstance())
                        .addToBackStack(null);

                transaction.commit();
*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStrBank.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView mItemBank;
        private TextView mNameBank;
        private ImageView mIconBank;
        public ViewHolder(View itemView) {
            super(itemView);

            mItemBank = (CardView) itemView.findViewById(R.id.item_bank);
            mNameBank = (TextView) itemView.findViewById(R.id.name_bank);
            mIconBank = (ImageView) itemView.findViewById(R.id.icon_bank);
        }
    }
}
