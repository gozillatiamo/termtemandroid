package com.worldwidewealth.termtem.dashboard.addcreditline.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.worldwidewealth.termtem.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEnterAgentPhone extends Fragment {

    private ViewHolder mHolder;
    public FragmentEnterAgentPhone() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_enter_agent_phone, container, false);
        mHolder = new ViewHolder(rootView);
        initBtn();
        return rootView;
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agentPhone = mHolder.mEditAgentPhone.getText().toString();
                if (agentPhone.equals("")){
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(mHolder.mEditAgentPhone);
                    Toast.makeText(getContext(),
                            getString(R.string.please_enter_agent_phone),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.container_add_credit, new FragmentAddCreditChoice())
                        .addToBackStack(null);
                transaction.commit();

            }
        });
    }

    private class ViewHolder{
        private AppCompatEditText mEditAgentPhone;
        private Button mBtnNext;
        public ViewHolder(View itemView) {
            mEditAgentPhone = (AppCompatEditText) itemView.findViewById(R.id.edit_agent_phone);
            mBtnNext = (Button) itemView.findViewById(R.id.btn_next);
        }
    }

}
