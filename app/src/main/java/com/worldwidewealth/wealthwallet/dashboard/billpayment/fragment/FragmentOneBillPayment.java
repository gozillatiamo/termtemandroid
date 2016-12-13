package com.worldwidewealth.wealthwallet.dashboard.billpayment.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.billpayment.adapter.AdapterListBillPreview;
import com.worldwidewealth.wealthwallet.dialog.DialogBillDetail;
import com.worldwidewealth.wealthwallet.until.Until;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOneBillPayment extends Fragment {

    private View rootview;
    private ListView mListBill;
    private AdapterListBillPreview mAdapter;
    public static FragmentOneBillPayment newInstance() {
        
        Bundle args = new Bundle();
        
        FragmentOneBillPayment fragment = new FragmentOneBillPayment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null){
            rootview = inflater.inflate(R.layout.fragment_one_bill_payment, container, false);
        }

        mListBill = (ListView) rootview.findViewById(R.id.listview_onebill);
        mAdapter = new AdapterListBillPreview(this.getContext());
        mListBill.setAdapter(mAdapter);
        Until.setListViewHeightBasedOnItems(mListBill);
        mListBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogBillDetail dialog = new DialogBillDetail(FragmentOneBillPayment.this.getContext());
                dialog.show();
            }
        });
        return rootview;
    }

}
