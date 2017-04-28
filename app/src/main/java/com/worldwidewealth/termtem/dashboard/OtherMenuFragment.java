package com.worldwidewealth.termtem.dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.widgets.MenuButtonView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<UserMenuModel> mUserMenuList;
    private MenuButtonView mMenuScan, mMenuSetUp, mMenuSupport;
    private Button mBtnBackToMain;


    public OtherMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OtherMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherMenuFragment newInstance() {
        OtherMenuFragment fragment = new OtherMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserMenuList = Global.getInstance().getUserMenuList();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_menu, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindView();
        setupMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getContext()).findViewById(R.id.logo_menu).setVisibility(View.GONE);
        mMenuSupport.dismiss();
        mMenuSetUp.dismiss();

    }

    private void bindView(){
        mMenuScan = (MenuButtonView) getView().findViewById(R.id.mbv_scan);
        mMenuSetUp = (MenuButtonView) getView().findViewById(R.id.mbv_setup);
        mMenuSupport = (MenuButtonView) getView().findViewById(R.id.mbv_support);
        mBtnBackToMain = (Button) getView().findViewById(R.id.btn_back_to_dashboard);
        mBtnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }
    private void setupMenu(){
        if (mUserMenuList == null) return;
        for (UserMenuModel model : mUserMenuList){
            MenuButtonView.TYPE type = MenuButtonView.TYPE.asTYPE(model.getBUTTON());
            if (type != null) {

                switch (type) {
                    case SCAN:
                        mMenuScan.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case SETUP:
                        mMenuSetUp.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case SUPPORT:
                        mMenuSupport.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                }
            }
        }

    }
}
