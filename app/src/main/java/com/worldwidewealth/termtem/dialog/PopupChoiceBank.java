package com.worldwidewealth.termtem.dialog;

import android.content.ContentValues;
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

import com.worldwidewealth.termtem.R;

import java.util.List;

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
    private ContentValues mBankSelected = null;
    private List<ContentValues> mListBank;

    public static final String KEY_NAME = "keyname";
    public static final String KEY_CODE = "keycode";
    public static final String KEY_ICON = "keyicon";

    public static final String TAG = PopupChoiceBank.class.getSimpleName();

    public PopupChoiceBank(Context context, View mRootView, List<ContentValues> listBank) {
        this.mRootView = mRootView;
        this.mContext = context;
        this.mListBank = listBank;
        mHolder = new ViewHolder(this.mRootView);
        initPopupBank();
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.showAsDropDown(mHolder.itemView);
            }
        });
    }

    public boolean isShow(){
        return mPopupMenu.isShowing();
    }

    public String getBank(){
        return mStrBank;
    }

    public ContentValues getValuesBankSelected(){
        return mBankSelected;
    }

    private void initPopupBank(){
        mRecyclerListBank = new RecyclerView(mContext);
        mRecyclerListBank.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerListBank.setAdapter(new BankListAdapter());

        mPopupMenu = new PopupWindow(
                mRecyclerListBank,
                ViewGroup.LayoutParams.MATCH_PARENT,
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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_bank, parent, false);
            ViewHolder holder = new ViewHolder(rootView);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextListBank.setText(mListBank.get(position).getAsString(KEY_NAME));
            holder.mIconListBank.setImageResource(mListBank.get(position).getAsInteger(KEY_ICON));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHolder.mTextBank.setText(mListBank.get(position).getAsString(KEY_CODE));
                    mHolder.mIconBank.setImageResource(mListBank.get(position).getAsInteger(KEY_ICON));
                    mStrBank = mListBank.get(position).getAsString(KEY_CODE);
                    mBankSelected = mListBank.get(position);
                    mHolder.mIconBank.setVisibility(View.VISIBLE);
                    mPopupMenu.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListBank.size();
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
