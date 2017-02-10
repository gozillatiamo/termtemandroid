package com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.FragmentTopupPreview;
import com.worldwidewealth.termtem.dashboard.mPayStation.MPayStationActivity;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dashboard.widgets.SelectAmountAndOtherFragment;
import com.worldwidewealth.termtem.until.BottomAction;

import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddCreditChoice extends Fragment {
    public static final String TAG = FragmentAddCreditChoice.class.getSimpleName();

    private String[] mAmount;
    private ViewHolder mHolder;
    private BottomAction mBottomAction;
    public FragmentAddCreditChoice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_credit_choice, container, false);
        mHolder = new ViewHolder(rootView);
        mAmount = new String[]{
                "100",
                "200",
                "300",
                "400",
                "500",
                "600",
                "700",
                "800",
                "900",
                "1000",
                "2000",
                getString(R.string.other)
        };
        initBtn();
        initGrid();

        return rootView;
    }


    private void initBtn(){
        mBottomAction = new BottomAction(getContext(),
                mHolder.mIncludeBottom,
                BottomAction.NEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomAction.setEnable(false);
                if (Float.parseFloat(mBottomAction.getPrice()) == 0){
                    Toast.makeText(getContext(), R.string.please_choice_topup, Toast.LENGTH_LONG).show();
                    mBottomAction.setEnable(true);
                    return;
                }

                if (Double.parseDouble(mBottomAction.getPrice()) < 1 || Double.parseDouble(mBottomAction.getPrice()) > 49000){
                    Toast.makeText(getContext(), getString(R.string.alert_amount_mpay_over), Toast.LENGTH_LONG).show();
                    return;
                }

                FragmentTransaction transaction = getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container_select_amount, FragmentTopupPreview.newInstance(null))
                        .addToBackStack(null);
                transaction.commit();


                mBottomAction.swichType(BottomAction.SUBMIT, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomAction.setEnable(false);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right,
                                        R.anim.slide_out_left)
                                .replace(R.id.container_add_credit, FragmentTopupSlip.newInstance(null, null))
                                .commit();
                    }
                });

            }
        });
    }

    private void initGrid(){
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.container_select_amount, SelectAmountAndOtherFragment.newInstance(mBottomAction, mAmount))
                .commit();
    }


    private class ViewHolder{
        private View mIncludeBottom;
        public ViewHolder(View itemView) {
            mIncludeBottom = (View) itemView.findViewById(R.id.include_bottom_action);
        }
    }

}
