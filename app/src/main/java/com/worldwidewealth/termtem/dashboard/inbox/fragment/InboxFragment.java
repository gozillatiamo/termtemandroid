package com.worldwidewealth.termtem.dashboard.inbox.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PAGE_TYPE = "pagetype";
    public static final int ALL = 0;
    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int VIDEO = 3;

    private RecyclerView mInboxRecycler;

    private int mPage;

    // TODO: Rename and change types of parameters


    public InboxFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InboxFragment newInstance(int type) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(PAGE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }


}
