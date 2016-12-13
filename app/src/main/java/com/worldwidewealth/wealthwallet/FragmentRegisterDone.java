package com.worldwidewealth.wealthwallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentRegisterDone extends Fragment {

    private View rootView;
    private ViewHolder mHolder;

    public static Fragment newInstance(){
        FragmentRegisterDone fragment = new FragmentRegisterDone();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_register_done, null, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentRegisterDone.this.getFragmentManager().popBackStack(0, 0);
            }
        });

        return rootView;
    }

    public class ViewHolder{

        private Button mBtnBack;
        public ViewHolder(View view){

            mBtnBack = (Button) view.findViewById(R.id.btn_back);
        }
    }

}
