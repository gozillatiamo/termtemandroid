package com.worldwidewealth.wealthcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentRegister extends Fragment {

    private View rootView;
    private ViewHolder mHolder;

    public static Fragment newInstance(){
        FragmentRegister fragment = new FragmentRegister();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_register, null, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = FragmentRegister2.this.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.dialog_enter, R.anim.dialog_exit, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.main_container, FragmentRegisterDone.newInstance())
                        .addToBackStack(null);

                transaction.commit();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.type_people_dropdown, R.layout.text_spinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mSpinnerTypePeople.setAdapter(adapter);
        mHolder.mSpinnerTypePeople.setSelection(0);


        return rootView;
    }

    public class ViewHolder{

        private Button mBtnNext;
        private Spinner mSpinnerTypePeople;
        public ViewHolder(View view){

            mBtnNext = (Button) view.findViewById(R.id.btn_next);
            mSpinnerTypePeople = (Spinner) view.findViewById(R.id.spinner_type_people);
        }
    }

}
