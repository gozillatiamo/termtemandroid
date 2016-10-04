package com.worldwidewealth.wealthwallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentRegister1 extends Fragment {

    private View rootView;
    private ViewHolder mHolder;

    public static Fragment newInstance(){
        FragmentRegister1 fragment = new FragmentRegister1();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_register1, null, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = FragmentRegister1.this.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.main_container, FragmentRegister2.newInstance())
                        .addToBackStack(null);

                transaction.commit();
            }
        });

        return rootView;
    }

    public class ViewHolder{

        private Button mBtnNext;
        public ViewHolder(View view){

            mBtnNext = (Button) view.findViewById(R.id.btn_next);
        }
    }

}
