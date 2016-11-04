package com.worldwidewealth.wealthcounter;

import android.content.ContentValues;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.worldwidewealth.wealthcounter.until.CheckSyntaxData;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentRegister extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    private static final int EMAIL = 0;
    private static final int NAME = 1;
    private static final int TEL = 2;
    private static final int IDEN = 3;
    private static final int PEOPLE = 4;
    private String mEmail, mName, mTel, mIden, mPeople;
    private boolean[] mDataCheck = new boolean[5];

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

        initPeople();
        initNext();


        return rootView;
    }


    private void initPeople(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.type_people_dropdown, R.layout.text_spinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mSpinnerTypePeople.setAdapter(adapter);
        mHolder.mSpinnerTypePeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean check = false;
                if (position != 0) check = true;
                mDataCheck[PEOPLE] = check;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mHolder.mSpinnerTypePeople.setSelection(0);

    }

    private void initNext(){

        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (boolean check: mDataCheck){
                    if (!check){
                        Toast.makeText(FragmentRegister.this.getContext(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                mEmail = mHolder.mEditEmail.getText().toString();
                mName = mHolder.mEditName.getText().toString();
                mTel = mHolder.mEditTel.getText().toString();
                mIden = mHolder.mEditIdentification.getText().toString();
                mPeople = mHolder.mSpinnerTypePeople.getSelectedItem().toString();

                Log.e("RegisterData:", "E-mail:" + mEmail +"\n"+
                        "Name:" + mName +"\n"+
                        "Tel:" + mTel +"\n"+
                        "Iden:" + mIden +"\n"+
                        "People:" + mPeople);

                FragmentTransaction transaction = FragmentRegister.this.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.dialog_enter, R.anim.dialog_exit, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.main_container, FragmentRegisterDone.newInstance())
                        .addToBackStack(null);

                transaction.commit();
            }
        });

    }

    private TextWatcher onTextChanged(final EditText editText, final int type){
        Log.e("Method:", "onTextChanged");
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean check = false;

                switch (type){
                    case EMAIL:
                        check = CheckSyntaxData.isEmailValid(s.toString());
                        break;
                    case NAME:
                        if (s.toString().equals("")){
                            check = false;
                        } else check= true;
                        break;
                    case TEL:
                        check = s.length() == 10;
                        break;
                    case IDEN:
                        if (s.length() == 13){
                            check = CheckSyntaxData.isIdentificationValid(s.toString());
                        }
                        break;
                }
                Drawable imgCheck;

                if (check)
                    imgCheck = getContext().getResources().getDrawable( R.drawable.ic_check_circle );
                else
                    imgCheck = getContext().getResources().getDrawable( R.drawable.ic_cancel );

                editText.setCompoundDrawablesWithIntrinsicBounds(null, null, imgCheck, null);

                mDataCheck[type] = check;

            }
        };
    }


    public class ViewHolder{

        private Button mBtnNext;
        private EditText mEditEmail, mEditName, mEditTel, mEditIdentification;
        private Spinner mSpinnerTypePeople;
        public ViewHolder(View view){

            mBtnNext = (Button) view.findViewById(R.id.btn_next);
            mSpinnerTypePeople = (Spinner) view.findViewById(R.id.spinner_type_people);
            mEditEmail = (EditText) view.findViewById(R.id.edit_email);
            mEditEmail.addTextChangedListener(onTextChanged(mEditEmail, EMAIL));
            mEditName = (EditText) view.findViewById(R.id.edit_name);
            mEditName.addTextChangedListener(onTextChanged(mEditName, NAME));
            mEditTel = (EditText) view.findViewById(R.id.edit_tel);
            mEditTel.addTextChangedListener(onTextChanged(mEditTel, TEL));
            mEditIdentification = (EditText) view.findViewById(R.id.edit_identification);
            mEditIdentification.addTextChangedListener(onTextChanged(mEditIdentification, IDEN));
        }
    }

}
