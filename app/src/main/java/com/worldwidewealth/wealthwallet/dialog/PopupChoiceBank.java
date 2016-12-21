package com.worldwidewealth.wealthwallet.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment.FragmentReportMT;

/**
 * Created by user on 21-Dec-16.
 */

public class PopupChoiceBank {
    private View mRootView;
    private RecyclerView mRecyclerListBank;
    private Context mContext;
    private PopupWindow mPopupMenu;
    private ViewHolder mHolder;
    private String mStrBank = "";
    private int mPositionBank = -1;

    public PopupChoiceBank(Context context, View mRootView) {
        this.mRootView = mRootView;
        this.mContext = context;
        mHolder = new ViewHolder(this.mRootView);
        initPopupBank();
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenu.isShowing()){
                    mPopupMenu.dismiss();
                } else {
                    mPopupMenu.showAsDropDown(mHolder.itemView);
                }

            }
        });
    }

    public String getBank(){
        return mStrBank;
    }

    public int getPositionSelect(){
        return mPositionBank;
    }

    private void initPopupBank(){
        mRecyclerListBank = new RecyclerView(mContext);
        mRecyclerListBank.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerListBank.setAdapter(new BankListAdapter());

        mPopupMenu = new PopupWindow(
                mRecyclerListBank,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopupMenu.setBackgroundDrawable(mContext.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        mPopupMenu.setOutsideTouchable(true);
        mPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //TODO do sth here on dismiss
            }
        });

    }

    private class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.ViewHolder>{

        private String[] mListNameBank = mContext.getResources().getStringArray(R.array.list_name_bank);
        private String[] mListCodeBank = mContext.getResources().getStringArray(R.array.list_code_bank);
        private TypedArray mLisIconBank = mContext.getResources().obtainTypedArray(R.array.ic_list_bank);

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_bank, parent, false);
            ViewHolder holder = new ViewHolder(rootView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextListBank.setText(mListNameBank[position]);
            holder.mIconListBank.setImageDrawable(mLisIconBank.getDrawable(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHolder.mTextBank.setText((mListNameBank[position]));
                    mHolder.mIconBank.setImageDrawable(mLisIconBank.getDrawable(position));
                    mStrBank = mListCodeBank[position];
                    mPositionBank = position;
                    mHolder.mIconBank.setVisibility(View.VISIBLE);
                    mPopupMenu.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListNameBank.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView mTextListBank;
            private ImageView mIconListBank;
            public ViewHolder(View itemView) {
                super(itemView);
                mTextListBank = (TextView) itemView.findViewById(R.id.txt_list_bank);
                mIconListBank = (ImageView) itemView.findViewById(R.id.item_icon_bank);
            }
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIconBank;
        private TextView mTextBank;
        public ViewHolder(View itemView){
            super(itemView);
            mIconBank = (ImageView) itemView.findViewById(R.id.icon_bank);
            mTextBank = (TextView) itemView.findViewById(R.id.txt_bank);
        }
    }
}
