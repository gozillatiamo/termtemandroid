package com.worldwidewealth.wealthwallet.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 5/10/2559.
 */

public class FragmentCreditLimit extends Fragment {

    private RecyclerView mListCLmenu;
    private ViewStub mTitleView;
    public static Fragment newInstance(){
        FragmentCreditLimit fragment = new FragmentCreditLimit();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mListCLmenu = new RecyclerView(this.getContext());
        return mListCLmenu;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mTitleView = (ViewStub) this.getActivity().findViewById(R.id.title_container);
        mTitleView.setLayoutResource(R.layout.title_credit_limit);

    }
}
