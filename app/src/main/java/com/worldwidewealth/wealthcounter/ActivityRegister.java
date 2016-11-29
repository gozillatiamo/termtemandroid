package com.worldwidewealth.wealthcounter;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.worldwidewealth.wealthcounter.model.RegisterRequestModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.until.CheckSyntaxData;

import me.grantland.widget.AutofitTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityRegister extends AppCompatActivity {

    private View rootView;
    private ViewHolder mHolder;
    private static final int FIRSTNAME = 0;
    private static final int LASTNAME = 1;
    private static final int EMAIL = 2;
    private static final int TEL = 3;
    private static final int IDEN = 4;
    private static final int PEOPLE = 5;
    private String mEmail, mFirstName, mLastName, mTel, mIden;
    private int mPerson;
    private boolean[] mDataCheck = new boolean[6];
    private APIServices services;

//    public static Fragment newInstance(){
//        FragmentRegister fragment = new FragmentRegister();
//        return fragment;
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mHolder = new ViewHolder(this);
        services = APIServices.retrofit.create(APIServices.class);
        initToolbar();
        initPeople();
        initNext();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        if (rootView == null){
//            rootView = inflater.inflate(R.layout.activity_register, null, false);
//            mHolder = new ViewHolder(rootView);
//            rootView.setTag(mHolder);
//        } else mHolder = (ViewHolder) rootView.getTag();
//
//
//
//        return rootView;
//    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initPeople(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_people_dropdown, R.layout.text_spinner);

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
                        Toast.makeText(ActivityRegister.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                mEmail = mHolder.mEditEmail.getText().toString();
                mFirstName = mHolder.mEditFristName.getText().toString();
                mLastName = mHolder.mEditLastName.getText().toString();
                mTel = mHolder.mEditTel.getText().toString();
                mIden = mHolder.mEditIdentification.getText().toString();
                mPerson = mHolder.mSpinnerTypePeople.getSelectedItemPosition()-1;

                Log.e("RegisterData:", "E-mail:" + mEmail +"\n"+
                        "FirstName:" + mFirstName +"\n"+
                        "LastName:" + mLastName +"\n"+
                        "Tel:" + mTel +"\n"+
                        "Iden:" + mIden +"\n"+
                        "People:" + mPerson);

                Call<ResponseModel> call = services.SIGNUP(new RegisterRequestModel(new RegisterRequestModel.Data(
                        mFirstName,
                        mLastName,
                        mEmail,
                        mTel,
                        mIden,
                        mPerson
                )));

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        String Msg = response.body().getMsg();

                        if(Msg.equals("Success")){

                            AlertDialog alertdialog = new AlertDialog.Builder(ActivityRegister.this)
                                    .setMessage(R.string.register_done)
                                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityRegister.this.finish();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(ActivityRegister.this, Msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

//                FragmentTransaction transaction = FragmentRegister.this.getActivity()
//                        .getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.dialog_enter, R.anim.dialog_exit, R.anim.slide_in_left, R.anim.slide_out_right)
//                        .replace(R.id.main_container, FragmentRegisterDone.newInstance())
//                        .addToBackStack(null);
//
//                transaction.commit();
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
                    case TEL:
                        check = s.length() == 10;
                        break;
                    case IDEN:
                        if (s.length() == 13){
                            check = CheckSyntaxData.isIdentificationValid(s.toString());
                        }
                        break;
                    default:
                        if (s.toString().equals("")){
                            check = false;
                        } else check= true;
                        break;
                }
                Drawable imgCheck;

                if (check)
                    imgCheck = getResources().getDrawable( R.drawable.ic_check_circle );
                else
                    imgCheck = getResources().getDrawable( R.drawable.ic_cancel );

                editText.setCompoundDrawablesWithIntrinsicBounds(null, null, imgCheck, null);

                mDataCheck[type] = check;

            }
        };
    }


    public class ViewHolder{

        private Button mBtnNext;
        private EditText mEditEmail, mEditFristName, mEditLastName, mEditTel, mEditIdentification;
        private Spinner mSpinnerTypePeople;
        private Toolbar mToolbar;
        private AutofitTextView mBtnSignIn;
        public ViewHolder(final Activity view){

            mBtnNext = (Button) view.findViewById(R.id.btn_next);
            mSpinnerTypePeople = (Spinner) view.findViewById(R.id.spinner_type_people);
            mEditEmail = (EditText) view.findViewById(R.id.edit_email);
            mEditEmail.addTextChangedListener(onTextChanged(mEditEmail, EMAIL));
            mEditFristName = (EditText) view.findViewById(R.id.edit_name);
            mEditFristName.addTextChangedListener(onTextChanged(mEditFristName, FIRSTNAME));
            mEditLastName = (EditText) view.findViewById(R.id.edit_last_name);
            mEditLastName.addTextChangedListener(onTextChanged(mEditLastName, LASTNAME));
            mEditTel = (EditText) view.findViewById(R.id.edit_tel);
            mEditTel.addTextChangedListener(onTextChanged(mEditTel, TEL));
            mEditIdentification = (EditText) view.findViewById(R.id.edit_identification);
            mEditIdentification.addTextChangedListener(onTextChanged(mEditIdentification, IDEN));
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mBtnSignIn = (AutofitTextView) view.findViewById(R.id.btn_signin);
            mBtnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.finish();
                }
            });
        }
    }

}
