package com.worldwidewealth.wealthwallet.dashboard.billpayment.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 10/10/2559.
 */

public class FragmentBillPreview extends Fragment{
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentBillPreview fragment = new FragmentBillPreview();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnPayment;
        private Spinner mListCurrency;
        public ViewHolder(View itemview){
            mBtnPayment = (Button) itemview.findViewById(R.id.btn_payment);
            mListCurrency = (Spinner) itemview.findViewById(R.id.spinner);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_bill_preview, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mBtnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.dialog_enter, R.anim.dialog_exit)
//                        .replace(R.id.dashboard_container, FragmentBillSlip.newInstance())
//                        .addToBackStack(null);
//                transaction.commit();
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
