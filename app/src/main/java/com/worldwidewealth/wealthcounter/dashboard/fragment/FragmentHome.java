package com.worldwidewealth.wealthcounter.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.adapter.AdapterListDashboard;
import com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment.FragmentCreditLimit;

/**
 * Created by gozillatiamo on 10/4/16.
 */
public class FragmentHome extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    private TabLayout mTabMain;

    public static Fragment newInstance(){
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        mTabMain = (TabLayout) getActivity().findViewById(R.id.tab_main);
        mTabMain.setVisibility(View.VISIBLE);

        initListDashboard();

        return rootView;
    }

    private void initListDashboard(){

        mHolder.mBtnCreditLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = FragmentHome.this.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, FragmentCreditLimit.newInstance())
                        .addToBackStack(null);

                        transaction.commit();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        mHolder.mRecycler.setLayoutManager(linearLayoutManager);
        mHolder.mRecycler.setAdapter(new AdapterListDashboard(this.getContext()));

    }


    public class ViewHolder{

        private RecyclerView mRecycler;
        private View mBtnCreditLimit;

        public ViewHolder(View view){
            mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
            mBtnCreditLimit = (View) view.findViewById(R.id.btn_credit_limit);
        }
    }

}
