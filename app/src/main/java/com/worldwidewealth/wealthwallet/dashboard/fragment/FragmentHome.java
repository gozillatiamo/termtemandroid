package com.worldwidewealth.wealthwallet.dashboard.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.Adapter.AdapterListDashboard;
import com.worldwidewealth.wealthwallet.until.SimpleDividerItemDecoration;

/**
 * Created by gozillatiamo on 10/4/16.
 */
public class FragmentHome extends Fragment {

    private View rootView;
    private ViewHolder mHolder;

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

        initListDashboard();

        return rootView;
    }

    private void initListDashboard(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        mHolder.mRecycler.setLayoutManager(linearLayoutManager);
        mHolder.mRecycler.setAdapter(new AdapterListDashboard(this.getContext()));
    }


    public class ViewHolder{

        private RecyclerView mRecycler;

        public ViewHolder(View view){
            mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
        }
    }

}
