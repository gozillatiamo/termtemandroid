package com.worldwidewealth.termtem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.worldwidewealth.termtem.chat.ChatBotActivity;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogHelp;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.TermTemSignIn;
import com.worldwidewealth.termtem.util.Util;

public class MainActivity extends MyAppcompatActivity implements View.OnClickListener{

    private ViewHolder mHolder;
    private boolean isClicked = true;
    private String TAG = "Main";
    private APIServices services;
    private String mPhone, mPassword;
/*
    private SharedPreferences mShared;
    private Set<String> mSetHistoryUser;
    public static final String CACHEUSER = "cacheuser";
    public static final String USER = "user";
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mHolder = new ViewHolder(this);
        Util.setupUI(findViewById(R.id.layout_parent));
        services = APIServices.retrofit.create(APIServices.class);
        initEditText();
//        initBtn();

        Glide.with(this).load(R.raw.app_chatbox_button).asGif().dontTransform().into(mHolder.mTermTemImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHolder.mBtnLogin.setEnabled(true);
    }

    private boolean mFormatting;
    private void initEditText(){
        mHolder.mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mFormatting){
                    mFormatting = true;
                    PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.FORMAT_NANP);
                    mFormatting = false;
                }
            }
        });

        mHolder.mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO){
                    login();
                }
                return false;
            }
        });

//        mShared = getSharedPreferences(CACHEUSER, Context.MODE_PRIVATE);
//        mSetHistoryUser = new HashSet<>(mShared.getStringSet(USER, new HashSet<String>()));

        String[] cacheUser = Global.getInstance().getCacheUser();

        if (cacheUser.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                   cacheUser);
            mHolder.mPhone.setText(Global.getInstance().getLastUserLogin());
            Util.showSoftKeyboard(this, mHolder.mPassword);
            mHolder.mPhone.setAdapter(adapter);
            mHolder.mPhone.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP){
                        mHolder.mPhone.setText("");
                        mHolder.mPhone.showDropDown();
                    }

                    return false;
                }
            });

            mHolder.mPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mHolder.mPassword.requestFocus();
                }
            });
        }
    }

    private void login(){
        mPhone = mHolder.mPhone.getText().toString();
        mPassword = mHolder.mPassword.getText().toString();
        if (mPhone.equals("") || mPassword.equals("")) {
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_data), Toast.LENGTH_SHORT).show();
            return;
        }

//        mHolder.mBtnLogin.setEnabled(false);


        new TermTemSignIn(this, TermTemSignIn.TYPE.NEWLOGIN,
                new DialogCounterAlert.DialogProgress(this).show())
                .checkWifi(mPhone, mPassword);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (isClicked) {
            isClicked = false;

            switch (v.getId()) {
                case R.id.btn_login:
                    login();
                    break;
                case R.id.btn_register:
                    Intent intent = new Intent(MainActivity.this, ActivityRegister.class);
                    startActivity(intent);
                    break;
                case R.id.help:
                    new DialogHelp(MainActivity.this).show();
                    break;
                case R.id.view_chat_bot:
                    Trace trace = MyApplication.mPerformance.startTrace("CHATBOT");
//                    trace.start();
                    trace.incrementCounter("COME_IN");
//                    trace.stop();
                    startActivity(ChatBotActivity.create(this));
                    break;

            }

            isClicked = true;
        }
    }

    public class ViewHolder{

        private TextView mBtnRegister;
        private Button mBtnLogin;
        private EditText mPassword;
        private AutoCompleteTextView mPhone;
        private TextView mHelp;
        private LinearLayout mChatBotView;
        private ImageView mTermTemImage;

        public ViewHolder(AppCompatActivity view){

            mBtnRegister = (TextView) view.findViewById(R.id.btn_register);
            mBtnRegister.setOnClickListener(MainActivity.this);
            mBtnLogin = (Button) view.findViewById(R.id.btn_login);
            mBtnLogin.setOnClickListener(MainActivity.this);

            mPhone = (AutoCompleteTextView) view.findViewById(R.id.edit_phone);

            mPassword = (EditText) view.findViewById(R.id.edit_password);
            mHelp = (TextView) view.findViewById(R.id.help);
            mHelp.setOnClickListener(MainActivity.this);

            mChatBotView = (LinearLayout) findViewById(R.id.view_chat_bot);
            mTermTemImage = (ImageView) findViewById(R.id.iv_chat_bot);
            mChatBotView.setOnClickListener(MainActivity.this);

        }
    }

}
