package com.worldwidewealth.wealthwallet.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.creditlimit.adapter.AdapterListCreditLimit;

/**
 * Created by MyNet on 5/10/2559.
 */

public class FragmentCreditLimit extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    private TabLayout mTabMain;

    public static Fragment newInstance(){
        FragmentCreditLimit fragment = new FragmentCreditLimit();
        return fragment;
    }

    public class ViewHolder{

        private RecyclerView mRecycler;

        public ViewHolder(View itemView){
            mRecycler = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_credit_limit, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } mHolder = (ViewHolder) rootView.getTag();

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//        mTabMain = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        mTabMain.setVisibility(View.VISIBLE);
        initListMenu();

        return rootView;
    }

    private void initListMenu(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mHolder.mRecycler.setLayoutManager(layoutManager);
        mHolder.mRecycler.setAdapter(new AdapterListCreditLimit(getContext()));
    }

}
