package com.worldwidewealth.wealthwallet.dashboard.creditlimit.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 6/10/2559.
 */

public class AdapterListCreditLimit extends RecyclerView.Adapter<AdapterListCreditLimit.ViewHolder>{

    private Context mContext;
    private String[] mStrList;
    private TypedArray mIconList;

    public AdapterListCreditLimit(Context context){
        this.mContext = context;
        this.mStrList = mContext.getResources().getStringArray(R.array.str_list_credit_limit);
        this.mIconList = mContext.getResources().obtainTypedArray(R.array.ic_list_credit_limit);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.column_menu_credit_limit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction;
                switch (pos){
                    case 0:
/*
                        transaction = ((AppCompatActivity)mContext).getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.dashboard_container, FragmentBank.newInstance())
                                .addToBackStack(null);

                        transaction.commit();
*/
                        break;
                    case 2:
/*
                        transaction = ((AppCompatActivity) mContext).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.dashboard_container, FragmentNotiPayment.newInstance())
                                .addToBackStack(null);

                        transaction.commit();
*/

                        break;
                }

            }
        });
        holder.mTitle.setText(mStrList[position]);
        holder.mIcon.setImageResource(mIconList.getResourceId(position, -1));
    }

    @Override
    public int getItemCount() {
        return mStrList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitle;
        private ImageView mIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title_menu);
            mIcon = (ImageView) itemView.findViewById(R.id.ic_list);
        }
    }

}
