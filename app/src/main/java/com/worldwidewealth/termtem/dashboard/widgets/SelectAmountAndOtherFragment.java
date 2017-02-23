package com.worldwidewealth.termtem.dashboard.widgets;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.util.BottomAction;
import com.worldwidewealth.termtem.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectAmountAndOtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectAmountAndOtherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_AMOUNT = "list_amount";
    public static final String TAG = SelectAmountAndOtherFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String[] mListAmount;

    private ViewHolder mHolder;
    private BottomAction mBottomAction;
    private View rootView;

    @SuppressLint("ValidFragment")
    public SelectAmountAndOtherFragment(BottomAction bottomAction) {
        this.mBottomAction = bottomAction;
    }

    public SelectAmountAndOtherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectAmountAndOtherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectAmountAndOtherFragment newInstance(BottomAction bottomAction, String[] listamount) {
        SelectAmountAndOtherFragment fragment = new SelectAmountAndOtherFragment(bottomAction);
        Bundle args = new Bundle();
        args.putStringArray(LIST_AMOUNT, listamount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListAmount = getArguments().getStringArray(LIST_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_select_amount_and_other, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mHolder == null) {
            mHolder = new ViewHolder(view);
            initListAmount();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBottomAction.getType() == BottomAction.SUBMIT) {
            mBottomAction.toggleType();
            mBottomAction.updatePrice(0);
            ((AmountBtnAdapter)mHolder.mRecyclerAmount.getAdapter()).clearSelected();
        }
    }


    private void initListAmount(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mHolder.mRecyclerAmount.setLayoutManager(gridLayoutManager);
        mHolder.mRecyclerAmount.setAdapter(new AmountBtnAdapter());

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView mRecyclerAmount;
        private View mLayoutEditAmountOther;
        private EditText mEditAmountOther;
        public ViewHolder(View itemView) {
            super(itemView);
            mRecyclerAmount = (RecyclerView) itemView.findViewById(R.id.recycler_amount);
            mLayoutEditAmountOther = (View) itemView.findViewById(R.id.layout_edit_amount_other);
            mEditAmountOther = (EditText) itemView.findViewById(R.id.edit_amount_other);

            mEditAmountOther.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    double amt = 0;
                    if (!s.toString().equals("") && !s.toString().equals(".")) {
                        amt = Double.parseDouble(s.toString());
                    }
                    mBottomAction.updatePrice(amt);
                }
            });
        }
    }


    private class AmountBtnAdapter extends RecyclerView.Adapter<AmountBtnAdapter.ViewHolder>{

        public int previousSelectedPosition = -1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_topup, parent, false);
            Util.setupUI(rootView);

            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextProductItem.setText(getItem(position));

            holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            if (position == previousSelectedPosition)
                setBackgroundSelect(holder, position);


            holder.mBtnChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickChoiceTopup(holder, position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mListAmount.length;
        }

        public String getItem(int position){
            return mListAmount[position];
        }

        private void setBackgroundSelect(ViewHolder holder, int position){
            holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.white));
            holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.white));
            holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));


            if (previousSelectedPosition == position) return;

            clearSelected();
        }

        public void clearSelected(){

            if (previousSelectedPosition != -1){
                ViewHolder holder = (ViewHolder)
                        mHolder.mRecyclerAmount.findViewHolderForAdapterPosition(previousSelectedPosition);
                if (holder == null) return;
                holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));

                mHolder.mLayoutEditAmountOther.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mHolder.mLayoutEditAmountOther.setVisibility(View.INVISIBLE);
                                mHolder.mRecyclerAmount.setFocusable(true);
                                mHolder.mRecyclerAmount.requestFocus();
                            }
                        });
                previousSelectedPosition = -1;
            }

        }

        private void setClickChoiceTopup(ViewHolder holder, int position){

            String nowAmt = "0";
            if (position != -1) {
                if (previousSelectedPosition == position) return;
                setBackgroundSelect(holder, position);
                if (position != getItemCount()-1) {
                    nowAmt = getItem(position);
                } else {
                    mHolder.mEditAmountOther.setText("");
                    mHolder.mLayoutEditAmountOther.setVisibility(View.VISIBLE);
                    mHolder.mLayoutEditAmountOther.setAlpha(0.0f);
                    mHolder.mLayoutEditAmountOther.animate()
                            .alpha(1.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mHolder.mEditAmountOther.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(mHolder.mEditAmountOther, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });
                }
            }
            previousSelectedPosition = position;
            mBottomAction.updatePrice(Double.parseDouble(nowAmt));


        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            private TextView mTextProductItem, mTextCurency;
            private CardView mBtnChoice;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextProductItem = (TextView) itemView.findViewById(R.id.txt_product_item);
                mTextCurency = (TextView) itemView.findViewById(R.id.txt_currency);
                mBtnChoice = (CardView) itemView.findViewById(R.id.btn_choice);
            }
        }

    }


}
