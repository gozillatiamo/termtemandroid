package com.worldwidewealth.wealthwallet.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 6/10/2559.
 */

public class FragmentTransfer extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    private TabLayout mTabMain;
    public static Fragment newInstance(){
        FragmentTransfer fragment = new FragmentTransfer();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnTransfer;
        private Spinner mListCurrency;

        public ViewHolder(View itemView){
            mBtnTransfer = (Button) itemView.findViewById(R.id.btn_transfer);
            mListCurrency = (Spinner) itemView.findViewById(R.id.spinner);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_transfer, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("เติมเงินในเครดิต");

//        mTabMain = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        mTabMain.setVisibility(View.VISIBLE);
        mHolder.mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                FragmentTransaction transaction = FragmentTransfer.this.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, FragmentPreview.newInstance())
                        .addToBackStack(null);

                transaction.commit();

*/
            }
        });
        initSpinnerCurrency();
        return rootView;
    }

    private void initSpinnerCurrency(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mListCurrency.setAdapter(adapter);
        mHolder.mListCurrency.setSelection(0);

    }



}
