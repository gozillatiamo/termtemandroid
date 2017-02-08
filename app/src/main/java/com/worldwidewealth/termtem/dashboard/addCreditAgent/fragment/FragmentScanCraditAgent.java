package com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.worldwidewealth.termtem.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentScanCraditAgent extends Fragment {

    private ViewHolder mHolder;

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container_add_credit, new FragmentEnterAgentPhone())
                        .addToBackStack(null);
                transaction.commit();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    public FragmentScanCraditAgent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_cradit_agent, container, false);
        mHolder = new ViewHolder(rootView);
//        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
//        intentIntegrator.forSupportFragment(this).initiateScan();
        mHolder.mBarcodeView.decodeSingle(barcodeCallback);
        return rootView;
    }

    @Override
    public void onResume() {
        mHolder.mBarcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mHolder.mBarcodeView.pause();
        super.onPause();
    }

    private class ViewHolder{
        private DecoratedBarcodeView mBarcodeView;
        public ViewHolder(View itemView) {
            mBarcodeView = (DecoratedBarcodeView) itemView.findViewById(R.id.barcode_view);

        }
    }

}
