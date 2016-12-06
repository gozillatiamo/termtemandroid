package com.worldwidewealth.wealthcounter.dashboard.addcreditline.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dialog.DialogCounterAlert;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentScanCraditAgent extends Fragment {

    private ViewHolder mHolder;

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null){
                new DialogCounterAlert(getContext(), null, result.getText());
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
        View rootView = inflater.inflate(R.layout.fragment_fragment_scan_cradit_agent, container, false);
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
