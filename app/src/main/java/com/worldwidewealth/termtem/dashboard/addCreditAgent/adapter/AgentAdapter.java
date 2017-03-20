package com.worldwidewealth.termtem.dashboard.addCreditAgent.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.model.AgentResponse;

import java.util.List;

/**
 * Created by user on 14-Feb-17.
 */

public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.ViewHolder>{

    private Context mContext;
    private List<AgentResponse> mDatatList;
    public static final String AGENTDATA = "agentdata";

    public AgentAdapter(Context context, List<AgentResponse> datalist){
        this.mContext = context;
        this.mDatatList = datalist;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);

        holder.itemView.setClickable(true);
        holder.itemView.setBackgroundResource(typedValue.resourceId);

        holder.mTitleNotify.setText(getItem(position).getFirstName() + "\t" + getItem(position).getLastName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyApplication.clickable) return;

                MyApplication.clickable = false;
                Bundle bundle = new Bundle();
                bundle.putParcelable(AGENTDATA, getItem(position));
                bundle.writeToParcel(Parcel.obtain(), 0);

                FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container_add_credit, FragmentAddCreditChoice.newInstance(bundle))
                        .addToBackStack(null);
                transaction.commit();

                MyApplication.clickable = true;

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatatList.size();
    }

    public AgentResponse getItem(int postion){
        return mDatatList.get(postion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIconNotify;
        private TextView mTitleNotify, mDesNotify, mTextDate;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitleNotify = (TextView) itemView.findViewById(android.R.id.text1);

/*
            mIconNotify = (ImageView) itemView.findViewById(R.id.icon_notify);
            mIconNotify.setVisibility(View.GONE);
            mTitleNotify = (TextView) itemView.findViewById(R.id.title_notify);
            mDesNotify = (TextView) itemView.findViewById(R.id.des_notify);
            mDesNotify.setVisibility(View.GONE);
            mTextDate = (TextView) itemView.findViewById(R.id.txt_date);
            mTextDate.setVisibility(View.GONE);
*/
        }
    }
}
