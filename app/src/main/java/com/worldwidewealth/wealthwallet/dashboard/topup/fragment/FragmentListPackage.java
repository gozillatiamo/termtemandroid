package com.worldwidewealth.wealthwallet.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentListPackage extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(int resource){
        Bundle bundle = new Bundle();
        bundle.putInt("layout", resource);
        FragmentListPackage fragment = new FragmentListPackage();
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{
        public ViewHolder(View itemview){
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout = getArguments().getInt("layout");

            rootView = inflater.inflate(layout, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);


        return rootView;
    }

}
