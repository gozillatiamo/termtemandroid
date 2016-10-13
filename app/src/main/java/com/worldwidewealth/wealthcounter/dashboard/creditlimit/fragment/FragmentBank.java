package com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.dashboard.creditlimit.adapter.AdapterListBank;

/**
 * Created by MyNet on 6/10/2559.
 */

public class FragmentBank extends Fragment {

    private RecyclerView mListBank;

    public static Fragment newInstance(){
        FragmentBank fragment = new FragmentBank();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("บํญชีธนาคาร");
        mListBank = new RecyclerView(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mListBank.setLayoutManager(layoutManager);
        mListBank.setAdapter(new AdapterListBank(getContext()));
        return mListBank;
    }
}
