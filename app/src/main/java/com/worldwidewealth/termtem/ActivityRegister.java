package com.worldwidewealth.termtem;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.MenuPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.RegisterRequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.CheckSyntaxData;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import me.grantland.widget.AutofitTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityRegister extends MyAppcompatActivity {

    private View rootView;
    private ViewHolder mHolder;
    private static final int FIRSTNAME = 0;
    private static final int LASTNAME = 1;
    private static final int TEL = 2;
    private static final int IDEN = 3;
    private static final int PEOPLE = 4;
    private static final int EMAIL = 5;
    private String mEmail, mFirstName, mLastName, mTel, mIden;
    private int mPerson;
    private boolean[] mDataCheck = new boolean[6];
    private APIServices services;
    public static final String TAG = ActivityRegister.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mHolder = new ViewHolder(this);
        mDataCheck[EMAIL] = true;
        Util.setupUI(findViewById(R.id.layout_parent));
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.type_people_dropdown))/*{
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;            }
        }*/;
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(spinnerAdapter);
        listPopupWindow.setAnchorView(mHolder.mEditPeopleType);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                mHolder.mEditPeopleType.setText(tv.getText());
                mPerson = position;
                listPopupWindow.dismiss();
            }
        });


        mHolder.mEditPeopleType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    listPopupWindow.show();

                }
                return false;
            }
        });
/*
        mHolder.mEditPeopleType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });
*/
/*
        mHolder.mSpinnerTypePeople.setAdapter(spinnerAdapter);
        mHolder.mSpinnerTypePeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean check = false;
                if (position != 0){
                    check = true;
                    Drawable imgCheck = getResources().getDrawable( R.drawable.ic_check_circle );
                    mHolder.mLayoutSpinnerPeople.setCompoundDrawables(null, null, imgCheck, null);
                }
                    mDataCheck[PEOPLE] = check;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
//        mHolder.mSpinnerTypePeople.setSelection(0);

    }

    private void initNext(){

        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (boolean check: mDataCheck){
                    Log.e(TAG, ""+check);
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
//                mPerson = mHolder.mSpinnerTypePeople.getSelectedItemPosition()-1;

                if (!mEmail.equals("")){
                    if (!CheckSyntaxData.isEmailValid(mEmail)){
                        Toast.makeText(ActivityRegister.this, getString(R.string.email_syntax_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (!mHolder.mCheckService.isChecked()){
                    Toast.makeText(ActivityRegister.this, getString(R.string.submit_service_please), Toast.LENGTH_LONG).show();
                    return;

                }
                new DialogCounterAlert.DialogProgress(ActivityRegister.this);
                Call<ResponseModel> call = services.SIGNUP(new RegisterRequestModel(new RegisterRequestModel.Data(
                        mFirstName,
                        mLastName,
                        mEmail,
                        mTel,
                        mIden,
                        mPerson
                )));

                APIHelper.enqueueWithRetry(call, new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if(response.body().getStatus() == APIServices.SUCCESS){

                            final TextView message = new TextView(ActivityRegister.this);
                            message.setPadding(20, 20, 20, 20);
                            // i.e.: R.string.dialog_message =>
                            // "Test this dialog following the link to dtmilano.blogspot.com"
                            final SpannableString s =
                                    new SpannableString(ActivityRegister.this.getText(R.string.register_done));
                            Linkify.addLinks(s, Linkify.WEB_URLS);
                            message.setText(s);
                            message.setMovementMethod(LinkMovementMethod.getInstance());

                            AlertDialog alertdialog = new AlertDialog.Builder(ActivityRegister.this)
                                    .setView(message)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityRegister.this.finish();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(ActivityRegister.this, response.body().getMsg(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        DialogCounterAlert.DialogProgress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(ActivityRegister.this, call, this);
                    }
                });

            }
        });

    }

    private TextWatcher onTextChanged(final EditText editText, final int type){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    editText.setText(result);
                    editText.setSelection(result.length());
                    return;
                    // alert the user
                }

                boolean check = false;
                switch (type){

                    case TEL:
                        check = CheckSyntaxData.isPhoneValid(s.toString());
                        break;
                    case IDEN:
                        if (s.length() == 13){
                            check = CheckSyntaxData.isIdentificationValid(s.toString());
                        }
                        break;
                    case EMAIL:
                        if (s.toString().equals(""))
                            check = true;
                        else
                            check = CheckSyntaxData.isEmailValid(s.toString());
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

                if (type == EMAIL && s.toString().equals(""))
                    imgCheck = null;


                editText.setCompoundDrawablesWithIntrinsicBounds(null, null, imgCheck, null);

                mDataCheck[type] = check;

            }
        };
    }


    public class ViewHolder{

        private Button mBtnNext, mBtnCondition;
        private EditText mEditEmail, mEditFristName, mEditLastName, mEditTel, mEditIdentification,
        mEditPeopleType;
//        private Spinner mSpinnerTypePeople;
        private Toolbar mToolbar;
        private AutofitTextView mBtnSignIn;
        private CheckBox mCheckService;
        public ViewHolder(final Activity view){

            mBtnNext = (Button) view.findViewById(R.id.btn_next);
//            mSpinnerTypePeople = (Spinner) view.findViewById(R.id.spinner_type_people);
            mEditEmail = (EditText) view.findViewById(R.id.edit_email);
            mEditEmail.addTextChangedListener(onTextChanged(mEditEmail, EMAIL));

//            mEditEmail.setOnFocusChangeListener(Util.onFocusEditText());
            mEditFristName = (EditText) view.findViewById(R.id.edit_name);
//            mEditFristName.setOnFocusChangeListener(Util.onFocusEditText());
            mEditFristName.addTextChangedListener(onTextChanged(mEditFristName, FIRSTNAME));
            mEditLastName = (EditText) view.findViewById(R.id.edit_last_name);
//            mEditLastName.setOnFocusChangeListener(Util.onFocusEditText());
            mEditLastName.addTextChangedListener(onTextChanged(mEditLastName, LASTNAME));
            mEditTel = (EditText) view.findViewById(R.id.edit_tel);
//            mEditTel.setOnFocusChangeListener(Util.onFocusEditText());
            mEditTel.addTextChangedListener(onTextChanged(mEditTel, TEL));
            mEditIdentification = (EditText) view.findViewById(R.id.edit_identification);
//            mEditIdentification.setOnFocusChangeListener(Util.onFocusEditText());
            mEditIdentification.addTextChangedListener(onTextChanged(mEditIdentification, IDEN));
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mBtnSignIn = (AutofitTextView) view.findViewById(R.id.btn_signin);
            mCheckService = (CheckBox) view.findViewById(R.id.check_submit);
            mBtnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.finish();
                }
            });
            mBtnCondition = (Button) view.findViewById(R.id.btn_condition_termtem);
            mEditPeopleType = (EditText) view.findViewById(R.id.edit_people_type);
            mEditPeopleType.addTextChangedListener(onTextChanged(mEditPeopleType, PEOPLE));
            mBtnCondition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://180.128.21.81/wealthweb/terms.htm"));
                    startActivity(i);
                }
            });
        }
    }

}
