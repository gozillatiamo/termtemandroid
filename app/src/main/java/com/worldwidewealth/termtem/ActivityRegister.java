package com.worldwidewealth.termtem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
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
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.RegisterRequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.CheckSyntaxData;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.TermTemLoading;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.grantland.widget.AutofitTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityRegister extends MyAppcompatActivity implements View.OnTouchListener, View.OnClickListener{

    private View rootView;
    private ViewHolder mHolder;
    private static final int TITLENAME = 0;
    private static final int FIRSTNAME = 1;
    private static final int LASTNAME = 2;
    private static final int BIRTH = 3;
    private static final int TEL = 4;
    private static final int IDEN = 5;
    private static final int ATTACH = 6;
    private static final int PEOPLE = 7;
    private static final int EMAIL = 8;
    private String mEmail, mFirstName, mLastName, mTel, mIden;
    private Dialog mDialogCondition;
    private DatePickerDialog mDateDialog;
    private int mPerson = 0;
    private boolean[] mDataCheck = new boolean[9];
    private APIServices services;
    private TermTemLoading mLoading;
    private Calendar mCalendar = Calendar.getInstance();
    private static Uri photoURI;
    private String imgPath;
    private Bitmap mBitmapImage;


    public static final String TAG = ActivityRegister.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mHolder = new ViewHolder(this);
        mDataCheck[EMAIL] = true;
        mDataCheck[PEOPLE] = true;
        Util.setupUI(findViewById(R.id.layout_parent));
        services = APIServices.retrofit.create(APIServices.class);
        initToolbar();
        initNext();
        setupDialogCondition();
        setupCalendar();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case MyApplication.REQUEST_IMAGE_CAPTURE:
//                    Bundle extras = data.getExtras();
//                    uri = Util.getImageUri((Bitmap) extras.get("data"));

                    imgPath =  Util.getRealPathFromURI(photoURI);


                    break;
                case MyApplication.REQUEST_IMAGE_CHOOSE:
                    photoURI = data.getData();
                    imgPath = Util.getRealPathFromURI(photoURI);

                    photoURI.toString().replace("com.android.gallery3d","com.google.android.gallery3d");

                    if (photoURI.toString().startsWith("content://com.google.android.gallery3d")
                            || photoURI.toString().startsWith("content://com.sec.android.gallery3d.provider") ) {

                        imgPath = Util.getPicasaImage(photoURI);
                    }
                    else
                        imgPath = Util.getRealPathFromURI(photoURI);

//                    imgPath = Util.getRealPathFromURI(uri);

                    break;
            }

                if (imgPath != null) {
                    mHolder.mImageAttach.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imgPath)
                            .override(300, 300)
                            .crossFade()
                            .placeholder(R.drawable.ic_picture)
                            .into(mHolder.mImageAttach);

                    mBitmapImage = Util.flip(Util.decodeSampledBitmapFromResource(imgPath, 300, 300), imgPath);
                    mDataCheck[ATTACH] = true;
                }

        }

    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showDropDown(final EditText editText, int itemResource){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(itemResource));
        final ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(spinnerAdapter);
        listPopupWindow.setAnchorView(editText);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                editText.setText(tv.getText());
//                mPerson = position;
                listPopupWindow.dismiss();
            }
        });

        listPopupWindow.show();


    }

    private void initNext(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mDataCheck.length; i++){
                    Log.e(TAG, ""+mDataCheck[i]);
                    if (!mDataCheck[i]){
                        setupError(i);
                        return;
                    }
                }

                mEmail = mHolder.mEditEmail.getText().toString();
                mFirstName = mHolder.mEditFristName.getText().toString();
                mLastName = mHolder.mEditLastName.getText().toString();
                mTel = mHolder.mEditTel.getText().toString();
                mIden = mHolder.mEditIdentification.getText().toString();

                if (!mEmail.equals("")){
                    if (!CheckSyntaxData.isEmailValid(mEmail)){
                        Toast.makeText(ActivityRegister.this, getString(R.string.email_syntax_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                mDialogCondition.show();

            }
        });

    }

    private void setupDialogCondition(){
        if (mDialogCondition == null){
            mDialogCondition = new Dialog(this);
            mDialogCondition.setContentView(R.layout.dialog_condition);
            mDialogCondition.setTitle(R.string.title_condition);
            mDialogCondition.setCancelable(false);
            mDialogCondition.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            WebView webCodition = (WebView) mDialogCondition.findViewById(R.id.webview_condition);
            final CheckBox checkboxSubmit = (CheckBox) mDialogCondition.findViewById(R.id.check_submit);
             mDialogCondition.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     mDialogCondition.cancel();
                 }
             });

            webCodition.getSettings().setJavaScriptEnabled(true);
            webCodition.setWebChromeClient(new WebChromeClient());
            webCodition.loadUrl("https://wwwealth.co/wealthservice/tc.html");

            checkboxSubmit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        checkboxSubmit.toggle();
                        mDialogCondition.cancel();

                        if (mLoading == null){
                            mLoading = new TermTemLoading(ActivityRegister.this, (ViewGroup) findViewById(R.id.layout_parent));
                        }

                        mLoading.show();

                        Call<ResponseModel> call = services.SIGNUP(new RegisterRequestModel(new RegisterRequestModel.Data(
                                mHolder.mEditTitleName.getText().toString(),
                                mFirstName,
                                mLastName,
                                mCalendar.getTimeInMillis(),
                                mEmail,
                                mTel,
                                mIden,
                                mPerson,
                                Util.encodeBitmapToUpload(mBitmapImage)
                        )));

                        APIHelper.enqueueWithRetry(call, new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                                if(response.body().getStatus() == APIServices.SUCCESS){

                                    final TextView message = new TextView(ActivityRegister.this);
                                    message.setPadding(20, 20, 20, 20);
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
                                    alertdialog.setOnShowListener(new MyShowListener());

                                } else {
                                    Toast.makeText(ActivityRegister.this, response.body().getMsg(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                mLoading.hide();

                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                mLoading.hide();
                                new ErrorNetworkThrowable(t).networkError(ActivityRegister.this, call, this);
                            }
                        });

                    }
                }
            });

        }

    }

    private void setupError(int type){
        Drawable imgCheck = getResources().getDrawable( R.drawable.ic_cancel );
        EditText editText = null;
        switch (type){
            case FIRSTNAME:
                editText = mHolder.mEditFristName;
                break;
            case LASTNAME:
                editText = mHolder.mEditLastName;
                break;
            case TEL:
                editText = mHolder.mEditTel;
                break;
            case IDEN:
                editText = mHolder.mEditIdentification;
                break;
            case PEOPLE:
                editText = mHolder.mEditPeopleType;
                break;
            case EMAIL:
                editText = mHolder.mEditEmail;
                break;
            case BIRTH:
                editText = mHolder.mEditBirth;
                break;
            case TITLENAME:
                editText = mHolder.mEditTitleName;
                break;
            case ATTACH:
                Toast.makeText(this, R.string.error_image_identity, Toast.LENGTH_SHORT).show();
                mHolder.mBtnAttach.requestFocus();
                break;

        }

        if (editText == null) return;
        Toast.makeText(ActivityRegister.this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, imgCheck, null);
        editText.requestFocus();


    }

    private void setupCalendar(){
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                mHolder.mEditBirth.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Util.hideSoftKeyboard(view);
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP:
                switch (view.getId()){
                    case R.id.edit_birthdate:
                        mDateDialog.show();
                        break;
                    case R.id.edit_title_name:
                        showDropDown((EditText) view, R.array.type_title_name);
                        break;
                    case R.id.edit_people_type:
                        showDropDown((EditText) view, R.array.type_people_dropdown);

                        break;
                }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_attach:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = Util.createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
//                        photoURI = FileProvider.getUriForFile(mFragment.getContext(),
//                                MyApplication.getContext().getPackageName(),
//                                photoFile);
                        photoURI = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, MyApplication.REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;
        }
    }


    public class ViewHolder{

        private Button mBtnNext, mBtnAttach;
        private ImageView mImageAttach;
        private EditText mEditEmail, mEditFristName, mEditLastName, mEditTel, mEditIdentification,
        mEditPeopleType, mEditBirth, mEditTitleName;
        private Toolbar mToolbar;
        private AutofitTextView mBtnSignIn;

        public ViewHolder(final Activity view){

            mBtnNext = (Button) view.findViewById(R.id.btn_next);
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
            mEditPeopleType = (EditText) view.findViewById(R.id.edit_people_type);
            mEditPeopleType.addTextChangedListener(onTextChanged(mEditPeopleType, PEOPLE));
            mEditPeopleType.setOnTouchListener(ActivityRegister.this);

            mEditBirth = (EditText) view.findViewById(R.id.edit_birthdate);
            mEditBirth.addTextChangedListener(onTextChanged(mEditBirth, BIRTH));
            mEditBirth.setOnTouchListener(ActivityRegister.this);

            mEditTitleName = (EditText) view.findViewById(R.id.edit_title_name);
            mEditTitleName.addTextChangedListener(onTextChanged(mEditTitleName, TITLENAME));
            mEditTitleName.setOnTouchListener(ActivityRegister.this);

            mBtnAttach = (Button) view.findViewById(R.id.btn_attach);
            mBtnAttach.setOnClickListener(ActivityRegister.this);

            mImageAttach = (ImageView) view.findViewById(R.id.image_identity);
        }
    }

}
