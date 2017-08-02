package com.worldwidewealth.termtem.chat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.worldwidewealth.termtem.ActivityRegister;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.chat.holder.CustomIncomingImageMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomIncomingTextMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomOutcomingImageMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomOutcomingTextMessageViewHolder;
import com.worldwidewealth.termtem.chat.model.Message;
import com.worldwidewealth.termtem.chat.model.User;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RegisterRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.CheckSyntaxData;
import com.worldwidewealth.termtem.util.Util;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Santipong on 5/23/2017.
 */

public class ChatBotActivity extends MyAppcompatActivity implements
        DateFormatter.Formatter, View.OnClickListener, MessagesListAdapter.OnMessageClickListener<Message>, View.OnKeyListener{

    private static final long MESSAGE_DELAY_TIME = 500;
    private static final int TUTORIAL_STEP = 0;
    private static final int HINTNAME_REGSTEP = 1;
    private static final int ADDNAME_REGSTEP = 2;
    private static final int BIRTHDAY_REGSTEP = 3;
    private static final int PHONENO_REGSTEP = 4;
    private static final int IDCARD_REGSTEP = 5;
    private static final int MGM_REGSTEP = 6;
    private static final int IMAGE_REGSTEP = 7;
    private static final int PREVIEW_REGSTEP = 8;
    private static final int PRECONFIRM_REGSTEP = 9;
    private static final int EDIT_REGSTEP = 10;
    private static final int ACCEPT_REGSTEP = 11;
    private static final int REMOVE_STEP = 99;
    private static final String LINK = "https://wwwealth.co/wealthservice/tc.html";
    private static final String IMG_BANK_ACCOUNT = "bank_account";
    private static final String IMG_MPAY = "mpay";
    private static final String IMG_ATM = "atm";
    private static final String IMG_TRANSFER = "transfer";
    private static String mCaptionMGM = "";

    private MessagesList messagesList;
    protected MessagesListAdapter<Message> messagesAdapter;
    private ImageLoader imageLoader;
    private Handler handler;
    private ImageView typingLeft;
    private ImageView typingRight;
    private ExpandableLayout expandableLayout;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog pickerDialog;
    private ImageView termTem;
    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private Dialog dialogWebView;
    private WebView webView;
    private APIServices service;

    //view first menu
    private ViewGroup inputTutorial;
    private Button btnRegister;
    private Button btnBank;
    private Button btnAccount;
    private Button btnFinish;

    //View transfer
    private ViewGroup inputTransfer;
    private ViewGroup inputMpay;
    private Button btnMpay;
    private Button btnMpayStation;
    private Button btnBankAccount;
    private Button btnATM;

    //view register
    private ViewGroup inputName;
    private ViewGroup inputBirth;
    private ViewGroup inputPhoneNumber;
    private ViewGroup inputIdCard;
    private ViewGroup inputAgentMGM;
    private ViewGroup inputImage;
    private ViewGroup inputConfirm;
    private ViewGroup inputEdit;
    private ViewGroup inputAccept;
    private ViewGroup inputSuccess;
    private ViewGroup inputTryAgain;
    private ViewGroup inputBack;
    private EditText edtName;
    private EditText edtLastName;
    private RadioGroup groupPrefixName;
    private Button btnConfirmName;
    private Button btnCancelName;
    private EditText edtBirth;
    private Button btnConfirmBirth;
    private Button btnCancelBirth;
    private Button btnConfirmPhone;
    private Button btnCancelPhone;
    private EditText edtPhoneNumber;
    private EditText edtIdCard;
    private Button btnConfirmId;
    private Button btnCancelId;
    private EditText edtAgentMGM;
    private Button btnConfirmAgentMGM;
    private Button btnSkipAgentMGM;
    private Button btnConfirmImage;
    private Button btnCancelImage;
    private ImageView idCardImage;
    private TextView tvCapture;
    private Button btnConfirmData;
    private Button btnEdit;
    private Button btnCancelData;
    private Button btnEditName;
    private Button btnEditBirth;
    private Button btnEditPhone;
    private Button btnEditIdCard;
    private Button btnEditAgentMGM;
    private Button btnEditIdcardImage;
    private Button btnEditCancel;
    private Button btnAccept;
    private Button btnNotAccept;
    private Button btnTryAgain;
    private Button btnEditTryAgain;
    private Button btnCancelTryAgain;
    private Button btnBye;
    private Button btnStay;
    private Button btnStepBack;

    private int step = TUTORIAL_STEP;
    private int registerStep = TUTORIAL_STEP;
    private int transferStep = TUTORIAL_STEP;
    private boolean isEdit = false;
    private boolean isClick = false;

    //variable for register
    private String prefixName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String idCard;
    private String phoneMGM;
    private Uri photoURI;
    private String imgPath;

    public static Intent create(Context context){
        return new Intent(context, ChatBotActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        service = APIServices.retrofit.create(APIServices.class);
        handler = new Handler();
        bindView();
        getMGMcaption();
        setUpView();
        initAdapter();
        createDialog();
        expandableLayout.collapse();

    }

    private void bindView(){
        messagesList = (MessagesList) findViewById(R.id.messagesList);
        typingLeft = (ImageView) findViewById(R.id.typing_left);
        typingRight = (ImageView) findViewById(R.id.typing_right);
        expandableLayout = (ExpandableLayout) findViewById(R.id.expandable_layout);
        termTem = (ImageView) findViewById(R.id.term_tem);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);

        inputTutorial = (ViewGroup) findViewById(R.id.input_tutorial);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnFinish = (Button) findViewById(R.id.btn_bye);
        btnAccount = (Button) findViewById(R.id.btn_transfer_account);
        btnBank = (Button) findViewById(R.id.btn_transfer_bank);

        inputName = (ViewGroup) findViewById(R.id.input_register_name);
        inputBirth = (ViewGroup) findViewById(R.id.input_birth);
        inputPhoneNumber = (ViewGroup) findViewById(R.id.input_phone);
        inputIdCard = (ViewGroup) findViewById(R.id.input_id_card);
        inputAgentMGM = (ViewGroup) findViewById(R.id.input_mgm);
        inputImage = (ViewGroup) findViewById(R.id.input_image);
        inputConfirm = (ViewGroup) findViewById(R.id.input_confirm_edit);
        inputEdit = (ViewGroup) findViewById(R.id.input_edit);
        inputAccept = (ViewGroup) findViewById(R.id.input_accept);
        inputTryAgain = (ViewGroup) findViewById(R.id.input_try_again);
        inputSuccess = (ViewGroup) findViewById(R.id.input_register_success);
        inputBack = (ViewGroup) findViewById(R.id.input_btn_back);

        edtName = (EditText) findViewById(R.id.name);
        edtLastName = (EditText) findViewById(R.id.surname);
        btnConfirmName = (Button) findViewById(R.id.btn_confirm);
        btnCancelName = (Button) findViewById(R.id.btn_cancel);
        groupPrefixName = (RadioGroup) findViewById(R.id.prefix_name);
        btnConfirmBirth = (Button) findViewById(R.id.btn_confirm_birth);
        btnCancelBirth = (Button) findViewById(R.id.btn_cancel_birth);
        edtBirth = (EditText) findViewById(R.id.birth);
        btnConfirmPhone = (Button) findViewById(R.id.btn_confirm_phone);
        btnCancelPhone = (Button) findViewById(R.id.btn_cancel_phone);
        edtPhoneNumber = (EditText) findViewById(R.id.phone_number);
        btnConfirmId = (Button) findViewById(R.id.btn_confirm_id);
        btnCancelId = (Button) findViewById(R.id.btn_cancel_id);
        edtIdCard = (EditText) findViewById(R.id.id_card);
        edtAgentMGM = (EditText) findViewById(R.id.phone_mgm);
        btnConfirmAgentMGM = (Button) findViewById(R.id.btn_confirm_mgm);
        btnSkipAgentMGM = (Button) findViewById(R.id.btn_skip_mgm);
        btnConfirmImage = (Button) findViewById(R.id.btn_confirm_image);
        btnCancelImage = (Button) findViewById(R.id.btn_cancel_image);
        tvCapture = (TextView) findViewById(R.id.text_capture);
        idCardImage = (ImageView) findViewById(R.id.id_card_image);
        btnConfirmData = (Button) findViewById(R.id.btn_confirm_edit);
        btnCancelData = (Button) findViewById(R.id.btn_cancel_edit);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEditName = (Button) findViewById(R.id.btn_edit_name);
        btnEditBirth = (Button) findViewById(R.id.btn_edit_birth);
        btnEditPhone = (Button) findViewById(R.id.btn_edit_phone_number);
        btnEditIdCard = (Button) findViewById(R.id.btn_edit_id_card);
        btnEditAgentMGM = (Button) findViewById(R.id.btn_edit_agent_mgm);
        btnEditIdcardImage = (Button) findViewById(R.id.btn_edit_id_card_image);
        btnEditCancel = (Button) findViewById(R.id.btn_edit_cancel);
        btnAccept = (Button) findViewById(R.id.btn_accept);
        btnNotAccept = (Button) findViewById(R.id.btn_un_accept);
        btnTryAgain = (Button) findViewById(R.id.btn_try_again);
        btnEditTryAgain = (Button) findViewById(R.id.btn_edit_try_again);
        btnCancelTryAgain = (Button) findViewById(R.id.btn_cancel_try_again);
        btnBye = (Button) findViewById(R.id.btn_exit);
        btnStay = (Button) findViewById(R.id.btn_stay);

        inputTransfer = (ViewGroup) findViewById(R.id.input_bank);
        inputMpay = (ViewGroup) findViewById(R.id.input_mpay);

        btnMpay = (Button) findViewById(R.id.btn_mpay);
        btnMpayStation = (Button) findViewById(R.id.btn_mpay_station);
        btnBankAccount = (Button) findViewById(R.id.btn_bank);
        btnATM = (Button) findViewById(R.id.btn_atm);
        btnStepBack = (Button) findViewById(R.id.btn_img_back);

    }

    private void setUpView(){
        //set image loader for load image in message list
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if (url.equalsIgnoreCase(IMG_BANK_ACCOUNT)){
                    Glide.with(getContext()).load(R.drawable.img_chat_tutorial_bank_account).diskCacheStrategy(DiskCacheStrategy.ALL ).into(imageView);
                }else if (url.equalsIgnoreCase(IMG_ATM)){
                    Glide.with(getContext()).load(R.drawable.img_chat_tutorial_atm).diskCacheStrategy(DiskCacheStrategy.ALL ).into(imageView);
                }else if (url.equalsIgnoreCase(IMG_MPAY)){
                    Glide.with(getContext()).load(R.drawable.img_chat_tutorial_mpay).diskCacheStrategy(DiskCacheStrategy.ALL ).into(imageView);
                }else if (url.equalsIgnoreCase(IMG_TRANSFER)){
                    Glide.with(getContext()).load(R.drawable.img_chat_tutorial_transfer_account).diskCacheStrategy(DiskCacheStrategy.ALL ).into(imageView);
                }else {
                    Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL ).into(imageView);
                }
            }
        };

        Glide.with(getContext()).load(R.raw.app_chatbox2).asGif().dontTransform().into(termTem);

        //set typing image
        Glide.with(getContext()).load(R.raw.typing).asGif().into(typingLeft);
        Glide.with(getContext()).load(R.raw.typing).asGif().into(typingRight);

        edtPhoneNumber.setOnKeyListener(this);
        edtIdCard.setOnKeyListener(this);

        //set on button click
        tvCapture.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnAccount.setOnClickListener(this);
        btnBank.setOnClickListener(this);
        btnConfirmName.setOnClickListener(this);
        btnCancelName.setOnClickListener(this);
        btnConfirmBirth.setOnClickListener(this);
        btnCancelBirth.setOnClickListener(this);
        btnConfirmPhone.setOnClickListener(this);
        btnCancelPhone.setOnClickListener(this);
        btnCancelId.setOnClickListener(this);
        btnConfirmId.setOnClickListener(this);
        btnConfirmAgentMGM.setOnClickListener(this);
        btnSkipAgentMGM.setOnClickListener(this);
        btnConfirmImage.setOnClickListener(this);
        btnCancelImage.setOnClickListener(this);
        idCardImage.setOnClickListener(this);
        btnConfirmData.setOnClickListener(this);
        btnCancelData.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnEditName.setOnClickListener(this);
        btnEditBirth.setOnClickListener(this);
        btnEditPhone.setOnClickListener(this);
        btnEditIdCard.setOnClickListener(this);
        btnEditAgentMGM.setOnClickListener(this);
        btnEditIdcardImage.setOnClickListener(this);
        btnEditCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnNotAccept.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);
        btnEditTryAgain.setOnClickListener(this);
        btnCancelTryAgain.setOnClickListener(this);
        btnBye.setOnClickListener(this);
        btnStay.setOnClickListener(this);

        btnBankAccount.setOnClickListener(this);
        btnMpayStation.setOnClickListener(this);
        btnMpay.setOnClickListener(this);
        btnATM.setOnClickListener(this);
        btnStepBack.setOnClickListener(this);

        setupCalendar();
        edtBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pickerDialog.show();
                return false;
            }
        });

        groupPrefixName.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio_mr){
                    prefixName = getString(R.string.chat_mr);
                }else if (checkedId == R.id.radio_ms){
                    prefixName = getString(R.string.chat_ms);
                }else if (checkedId == R.id.radio_miss){
                    prefixName = getString(R.string.chat_miss);
                }
            }
        });
    }

    private void getMGMcaption(){
        Call<ResponseBody> call = service.service(new RequestModel(APIServices.ACTION_MGM_CAPTION, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(ChatBotActivity.this, call, response.body(), this);
                if (objectResponse instanceof String){
                    try {
                        JSONObject json = new JSONObject((String) objectResponse);
                        mCaptionMGM = json.getString("caption");
                        btnEditAgentMGM.setText(mCaptionMGM);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mCaptionMGM = getString(R.string.hint_mgm);
                btnEditAgentMGM.setText(mCaptionMGM);
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (!isClick) {
            isClick = true;
            switch (v.getId()) {
                case R.id.btn_register:
                    if (isNetworkAvailable()) {
                        registerClick();
                    } else {
                        addTextMessageNotDelay(String.format("ต้องขอโทษด้วยนะครับ ตอนนี้น้องเติมเต็มไม่สามารถเชื่อมต่อสัญญาณอินเตอเน็ตได้ โปรดตรวจสอบการเชื่อมต่อในโทรศัพท์ของท่านด้วยครับ"),
                                User.getTermTemUser());
                        expandableLayout.collapse();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                inputTutorial.setVisibility(View.GONE);
                                inputEdit.setVisibility(View.GONE);
                                inputTryAgain.setVisibility(View.GONE);
                                enable(inputSuccess);
                                inputSuccess.setVisibility(View.VISIBLE);
                                expandableLayout.expand();
                            }
                        }, 1000);
                    }
                    break;
                case R.id.btn_transfer_bank:
                    transferBankClick();
                    break;
                case R.id.btn_transfer_account:
                    transferAccountClick();
                    break;
                case R.id.btn_bye:
                    byeClick();
                    break;
                case R.id.btn_cancel:
                    cancelClick();
                    break;
                case R.id.btn_confirm:
                    confirmNameClick();
                    break;
                case R.id.btn_confirm_birth:
                    confirmBirthClick();
                    break;
                case R.id.btn_cancel_birth:
                    cancelClick();
                    break;
                case R.id.edit_birthdate:
                    pickerDialog.show();
                    break;
                case R.id.btn_cancel_phone:
                    cancelClick();
                    break;
                case R.id.btn_confirm_phone:
                    confirmPhoneClick();
                    break;
                case R.id.btn_cancel_id:
                    cancelClick();
                    break;
                case R.id.btn_confirm_id:
                    confirmIdClick();
                    break;
                case R.id.btn_confirm_mgm:
                    confirmMGMClick();
                    break;
                case R.id.btn_skip_mgm:
                    skipMGMClick();
                    break;
                case R.id.btn_confirm_image:
                    confirmImageClick();
                    break;
                case R.id.btn_cancel_image:
                    cancelClick();
                    break;
                case R.id.id_card_image:
                    captureImageClick();
                    break;
                case R.id.btn_cancel_edit:
                    cancelRegisterClick();
                    break;
                case R.id.btn_confirm_edit:
                    confirmEditClick();
                    break;
                case R.id.btn_edit:
                    onEditClick();
                    break;
                case R.id.btn_edit_name:
                    onEditNameClick();
                    break;
                case R.id.btn_edit_birth:
                    onEditBirthClick();
                    break;
                case R.id.btn_edit_phone_number:
                    onEditPhoneNumberClick();
                    break;
                case R.id.btn_edit_id_card:
                    onEditIdCardClick();
                    break;
                case R.id.btn_edit_agent_mgm:
                    onEditMGMClick();
                    break;
                case R.id.btn_edit_id_card_image:
                    onEditIdCardImageClick();
                    break;
                case R.id.btn_edit_cancel:
                    cancelRegisterClick();
                    break;
                case R.id.btn_accept:
                    acceptRegister();
                    break;
                case R.id.btn_un_accept:
                    clearData();
                    cancelClick();
                    break;
                case R.id.btn_try_again:
                    acceptRegister();
                    break;
                case R.id.btn_edit_try_again:
                    onEditClick();
                    break;
                case R.id.btn_cancel_try_again:
                    cancelRegisterClick();
                    break;
                case R.id.btn_stay:
                    stayClick();
                    break;
                case R.id.btn_exit:
                    byeBye();
                    break;
                case R.id.btn_bank:
                    bankAccountClick();
                    break;
                case R.id.btn_mpay:
                    mPayClick();
                    break;
                case R.id.btn_mpay_station:
                    mPayStationClick();
                    break;
                case R.id.btn_atm:
                    atmClick();
                    break;
                case R.id.btnBack:
                    onBackPressed();
                    break;
                case R.id.btnRefresh:
                    startActivity(create(this));
                    finish();
                    break;
                case R.id.btn_img_back:
                    backClick();
                    break;
                case R.id.text_capture:
                    captureImageClick();
                    break;
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isClick = false;
            }
        }, 500);
    }

    private Context getContext(){
        return this;
    }

    private void initAdapter(){
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_custom_incoming_image_message)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_custom_outcoming_image_message);

        messagesAdapter = new MessagesListAdapter<>("0", holdersConfig, imageLoader);
        messagesAdapter.setDateHeadersFormatter(this);
        messagesAdapter.setOnMessageClickListener(this);
        messagesList.setAdapter(messagesAdapter);
        addTextMessage(getString(R.string.chat_hello_i_am_term_tem), User.getTermTemUser());
    }

    @Override
    public void onMessageClick(Message message) {
        if (message.getImageUrl()!=null){
            //show image
            if (message.getImageUrl().equalsIgnoreCase(IMG_BANK_ACCOUNT)){
                startActivity(PhotoViewActivity.create(this, R.drawable.img_chat_tutorial_bank_account));
            }else if (message.getImageUrl().equalsIgnoreCase(IMG_ATM)){
                startActivity(PhotoViewActivity.create(this, R.drawable.img_chat_tutorial_atm));
            }else if (message.getImageUrl().equalsIgnoreCase(IMG_MPAY)){
                startActivity(PhotoViewActivity.create(this, R.drawable.img_chat_tutorial_mpay));
            }else if (message.getImageUrl().equalsIgnoreCase(IMG_TRANSFER)){
                startActivity(PhotoViewActivity.create(this, R.drawable.img_chat_tutorial_transfer_account));
            }else {
                startActivity(PhotoViewActivity.create(this, message.getImageUrl()));
            }
        }else if (message.getUrl()!=null && !message.getUrl().equalsIgnoreCase("")){
            //show link
            webView.loadUrl(message.getUrl());
            dialogWebView.show();
        }
    }

    @Override
    public String format(Date date) {
       return "";
    }

    //Add message tex
    private void addTextMessage(final String text, final User user){
        if (!user.isMe()) {
            typingLeft.setVisibility(View.VISIBLE);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                typingLeft.setVisibility(View.GONE);
                messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                handleChatStep();
            }
        }, MESSAGE_DELAY_TIME);
    }

    //Add message tex
    private void addTextMessageNotDelay(String text, final User user){
        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
    }

    //Add message tex
    private void addTextMessageWithLink(final String text, final User user){
        if (!user.isMe()) {
            typingLeft.setVisibility(View.VISIBLE);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                typingLeft.setVisibility(View.GONE);
                messagesAdapter.addToStart(Message.getTextMessageWithUrl(user, text, LINK), true);
                addTextMessageNotDelay(String.format("คุณ%s ยอมรับเงื่อนไขการใช้บริการเติมเต็มหรือไม่ครับ", firstName), User.getTermTemUser());
                handleChatStep();
            }
        }, MESSAGE_DELAY_TIME);
    }

    //Add message image
    private void addImageMessage(final String url, final User user){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesAdapter.addToStart(Message.getImageMessage(user, url), true);
                handleChatStep();
            }
        }, MESSAGE_DELAY_TIME);
    }

    //Add message image
    private void addImageMessageNotDelay(final String url, final User user){
        messagesAdapter.addToStart(Message.getImageMessage(user, url), true);
    }

    private void handleChatStep(){
        switch (step) {
            case TUTORIAL_STEP:
                step = 1;
                addTextMessage(getString(R.string.chat_need_help), User.getTermTemUser());
                break;
            case 1:
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputConfirm.setVisibility(View.GONE);
                inputAccept.setVisibility(View.GONE);
                inputMpay.setVisibility(View.GONE);
                inputTransfer.setVisibility(View.GONE);
                inputAccept.setVisibility(View.GONE);
                inputTryAgain.setVisibility(View.GONE);
                inputSuccess.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputBack.setVisibility(View.GONE);
                inputTutorial.setVisibility(View.VISIBLE);
                enable(inputTutorial);
                expandableLayout.expand();
                break;
            case 2:
                byeBye();
                break;
            case 3:
                step = 4;
                transferStep = 5;
                addImageMessage(IMG_TRANSFER, User.getTermTemUser());
                inputTutorial.setVisibility(View.GONE);
                expandableLayout.collapse();
                break;
            case 4:
                handleTransferStep();
                break;
            case 5:
                handleRegister();
                break;
        }
    }

    private void handleRegister(){
        switch (registerStep){
            case TUTORIAL_STEP:
                registerStep = HINTNAME_REGSTEP;
                addTextMessage(getString(R.string.chat_register_tutorial), User.getTermTemUser());
                break;
            case HINTNAME_REGSTEP:
                registerStep = ADDNAME_REGSTEP;
                addTextMessage(getString(R.string.chat_add_name), User.getTermTemUser());
                break;
            case ADDNAME_REGSTEP:
                registerStep = REMOVE_STEP;
                enable(inputName);
                inputName.setVisibility(View.VISIBLE);
                inputEdit.setVisibility(View.GONE);
                inputTutorial.setVisibility(View.GONE);
                expandableLayout.expand();
                break;
            case BIRTHDAY_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputBirth.setVisibility(View.VISIBLE);
                enable(inputBirth);
                expandableLayout.expand();
                break;
            case PHONENO_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                enable(inputPhoneNumber);
                inputPhoneNumber.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case IDCARD_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                enable(inputIdCard);
                inputIdCard.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case MGM_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputAgentMGM.setVisibility(View.VISIBLE);

                enable(inputAgentMGM);
                expandableLayout.expand();
                break;
            case IMAGE_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputAgentMGM.setVisibility(View.GONE);
                enable(inputImage);
                inputImage.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case PREVIEW_REGSTEP:
                registerStep = PRECONFIRM_REGSTEP;
                String format = phoneMGM == null ? "%s%s %s\n%s : %s\n%s : %s\n%s : %s": "%s%s %s\n%s : %s\n%s : %s\n%s : %s\n%s : %s";
                addTextMessage(String.format(format,prefixName, firstName, lastName,
                        getString(R.string.hint_birthday), edtBirth.getText().toString(),
                        getString(R.string.phone_number), phoneNumber,
                        getString(R.string.identity_number), idCard,
                        mCaptionMGM, phoneMGM), User.getTermTemUser());
                break;
            case PRECONFIRM_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputAgentMGM.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);

                enable(inputConfirm);
//                addImageMessageNotDelay(imgPath, User.getTermTemUser());
                inputConfirm.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case EDIT_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputAgentMGM.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);
                inputConfirm.setVisibility(View.GONE);
                inputTryAgain.setVisibility(View.GONE);
                addTextMessage(String.format("คุณ%s ต้องการแก้ไขข้อมูลส่วนไหนครับ?", firstName), User.getTermTemUser());
                inputEdit.setVisibility(View.VISIBLE);
                enable(inputEdit);
                expandableLayout.expand();
                break;
            case ACCEPT_REGSTEP:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputAgentMGM.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);
                inputConfirm.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputSuccess.setVisibility(View.GONE);
                enable(inputAccept);
                inputAccept.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;

        }
    }

    private void handleTransferStep(){
        switch (transferStep){
            case TUTORIAL_STEP:
                transferStep = REMOVE_STEP;
                addTextMessageNotDelay("คุณต้องการทราบวิธีโอนเงินเข้าบัญชีเติมเต็มวิธีไหนครับ?", User.getTermTemUser());
                inputTutorial.setVisibility(View.GONE);
                inputTransfer.setVisibility(View.VISIBLE);
                enable(inputTransfer);
                expandableLayout.expand();
                break;
            case 1:
                transferStep = 5;
                addImageMessageNotDelay(IMG_BANK_ACCOUNT, User.getTermTemUser());
                addTextMessage("เมื่อโอนเงินแล้วต้องแจ้งโอนเงินในโปรแกรมด้วยนะครับ", User.getTermTemUser());
                expandableLayout.collapse();
                break;
            case 2:
                transferStep = REMOVE_STEP;
                inputTutorial.setVisibility(View.GONE);
                inputTransfer.setVisibility(View.GONE);
                inputMpay.setVisibility(View.VISIBLE);
                enable(inputMpay);
                expandableLayout.expand();
                break;
            case 3:
                transferStep = 5;
                addImageMessage(IMG_MPAY, User.getTermTemUser());
                expandableLayout.collapse();
                break;
            case 4:
                transferStep = 5;
                addImageMessage(IMG_ATM, User.getTermTemUser());
                expandableLayout.collapse();
                break;
            case 5:
                step = TUTORIAL_STEP;
                transferStep = TUTORIAL_STEP;
                inputTutorial.setVisibility(View.GONE);
                inputTransfer.setVisibility(View.GONE);
                inputMpay.setVisibility(View.GONE);
                inputBack.setVisibility(View.VISIBLE);
                enable(inputBack);
                expandableLayout.expand();
                break;
        }
    }

   private void registerClick(){
       disable(inputTutorial);
       step = 5;
       registerStep = TUTORIAL_STEP;
       addTextMessage(getString(R.string.chat_register_account), User.getUser());
       expandableLayout.collapse();
   }

   private void transferBankClick(){
       disable(inputTutorial);
       step = 3;
       addTextMessage(getString(R.string.chat_how_to_transfer_money_to_bank), User.getUser());
       expandableLayout.collapse();
   }

   private void transferAccountClick(){
       disable(inputTutorial);
       step = 4;
       addTextMessage(getString(R.string.chat_how_to_transfer_money_to_account), User.getUser());
       expandableLayout.collapse();
   }

   private void byeClick(){
       disable(inputTutorial);
       step = 2;
       addTextMessage(getString(R.string.chat_bye), User.getTermTemUser());
       expandableLayout.collapse();
   }

   private void byeBye(){
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               onBackPressed();
           }
       }, MESSAGE_DELAY_TIME);
   }

   private void cancelClick(){
       step = TUTORIAL_STEP;
       addTextMessage(getString(R.string.cancel), User.getUser());
       expandableLayout.collapse();
       clearData();
   }

    private void backClick(){
        step = TUTORIAL_STEP;
        addTextMessage("ย้อนกลับ", User.getUser());
        expandableLayout.collapse();
        clearData();
    }

    private void cancelRegisterClick(){
        disable(inputTryAgain);
        disable(inputConfirm);
        step = TUTORIAL_STEP;
        addTextMessageNotDelay("ยกเลิกการสมัคร", User.getUser());
        addTextMessageNotDelay("ขอบคุณครับ", User.getTermTemUser());
        expandableLayout.collapse();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inputConfirm.setVisibility(View.GONE);
                inputTryAgain.setVisibility(View.GONE);
                inputSuccess.setVisibility(View.VISIBLE);
                enable(inputSuccess);
                expandableLayout.expand();
            }
        },1000);
    }

    private void stayClick(){
        disable(inputSuccess);
        step = TUTORIAL_STEP;
        registerStep = TUTORIAL_STEP;
        clearData();
        addTextMessage("คุยต่อ", User.getUser());
        expandableLayout.collapse();
    }

   private void confirmNameClick(){
       disable(inputName);
        if (prefixName == null || prefixName.equalsIgnoreCase("")){
            enable(inputName);
            addTextMessage(getString(R.string.chat_please_select_prefix), User.getTermTemUser());
        }else if (edtName.getText().toString().trim().equals("")){
            enable(inputName);
            addTextMessage(getString(R.string.chat_please_enter_name), User.getTermTemUser());
        }else if (edtLastName.getText().toString().trim().equals("")){
            enable(inputName);
           addTextMessage(getString(R.string.chat_please_enter_last_name), User.getTermTemUser());
       }else {
            firstName = edtName.getText().toString().trim();
            lastName = edtLastName.getText().toString().trim();
            if (!isEdit) {
                registerStep = BIRTHDAY_REGSTEP;
                addTextMessageNotDelay(String.format("%s%s %s", prefixName, firstName, lastName), User.getUser());
                addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_enter_birthday)), User.getTermTemUser());
                expandableLayout.collapse();
            }else {
                onEditSuccess();
            }
        }
   }

   private void confirmBirthClick(){
       disable(inputBirth);
       if (edtBirth.getText().toString().trim().equals("")){
           enable(inputBirth);
           addTextMessage(getString(R.string.chat_please_enter_birth), User.getTermTemUser());
       }else {
           if (!isEdit) {
               registerStep = PHONENO_REGSTEP;
               addTextMessageNotDelay(edtBirth.getText().toString(), User.getUser());
               addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_enter_phone_number)), User.getTermTemUser());
               expandableLayout.collapse();
           }else {
               onEditSuccess();
           }
       }
   }

    private void confirmPhoneClick(){
        disable(inputPhoneNumber);
        if (edtPhoneNumber.getText().toString().trim().equals("")){
            enable(inputPhoneNumber);
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_phone)), User.getTermTemUser());
        }else if (!CheckSyntaxData.isPhoneValid(edtPhoneNumber.getText().toString())){
            enable(inputPhoneNumber);
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_phone_number_is_invalid)), User.getTermTemUser());
        }else {
            if (!isEdit) {
                registerStep = IDCARD_REGSTEP;
                phoneNumber = edtPhoneNumber.getText().toString();
                addTextMessageNotDelay(phoneNumber, User.getUser());
                addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_enter_id_card_number)), User.getTermTemUser());
                expandableLayout.collapse();
            }else {
                phoneNumber = edtPhoneNumber.getText().toString();
                onEditSuccess();
            }
        }
    }

    private void confirmIdClick(){
        disable(inputIdCard);
        if (edtIdCard.getText().toString().trim().equals("")){
            enable(inputIdCard);
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_id)), User.getTermTemUser());
        }else {
            if (edtIdCard.getText().toString().length()==13){
                if (!CheckSyntaxData.isIdentificationValid(edtIdCard.getText().toString())){
                    enable(inputIdCard);
                    addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_id_card_is_invalid)), User.getTermTemUser());
                }else {
                    if (!isEdit) {
                        registerStep = MGM_REGSTEP;
                        idCard = edtIdCard.getText().toString();
                        addTextMessageNotDelay(idCard, User.getUser());
                        addTextMessage(String.format("คุณ%s %s%s %s", firstName, getString(R.string.chat_enter_mgm_first),
                                mCaptionMGM, getString(R.string.chat_enter_mgm_second)), User.getTermTemUser());
                        expandableLayout.collapse();
                    }else {
                        idCard = edtIdCard.getText().toString();
                        onEditSuccess();
                    }
                }
            }else {
                enable(inputIdCard);
                addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_id)), User.getTermTemUser());
            }
        }
    }

    private void confirmMGMClick(){
        disable(inputAgentMGM);
        if (edtAgentMGM.getText().toString().trim().equals("")){
            enable(inputAgentMGM);
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_mgm)), User.getTermTemUser());
        }else {
            if (!isEdit) {
                registerStep = IMAGE_REGSTEP;
                phoneMGM = edtAgentMGM.getText().toString();
                addTextMessageNotDelay(phoneMGM, User.getUser());
                addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_please_select_image)), User.getTermTemUser());
                expandableLayout.collapse();
            }else {
                phoneMGM = edtAgentMGM.getText().toString();
                onEditSuccess();
            }
        }
    }

    private void skipMGMClick(){
        if (!isEdit) {
            registerStep = IMAGE_REGSTEP;
            phoneMGM = null;
            addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_please_select_image)), User.getTermTemUser());
            expandableLayout.collapse();
        }else {
            onEditSuccess();
        }
    }


    private void setupCalendar(){
        calendar.setTimeInMillis(System.currentTimeMillis());
        pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar currentDate = Calendar.getInstance();
                Calendar birthDate = Calendar.getInstance();
                birthDate.set(year, month, dayOfMonth);

                int age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

                age = (currentDate.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH)) ||
                        ((currentDate.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH)) &&
                                currentDate.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH)) ? --age:age;

                boolean yearOld = age >= 20;
                if (!yearOld){
                    addTextMessage("ผู้สมัครจะต้องมีอายุ 20 ปีบริบูรณ์จึงจะสมัครเป็นตัวแทนได้นะครับ", User.getTermTemUser());
                    return;
                }
                edtBirth.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    private void confirmImageClick(){
        disable(inputImage);
        if (imgPath==null || imgPath.equalsIgnoreCase("")){
            enable(inputImage);
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_select_image)), User.getTermTemUser());
        }else {
            if (!isEdit) {
                registerStep = PREVIEW_REGSTEP;
                addImageMessageNotDelay(imgPath, User.getUser());
                addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_please_check)), User.getTermTemUser());
                expandableLayout.collapse();
            }else {
                addImageMessageNotDelay(imgPath, User.getUser());
                onEditSuccess();
            }
        }
    }

    private void captureImageClick(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Util.createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, MyApplication.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void onEditSuccess(){
        registerStep = PREVIEW_REGSTEP;
        addTextMessage(String.format("คุณ%s %s",firstName, getString(R.string.chat_please_check)), User.getTermTemUser());
        expandableLayout.collapse();
    }

    private void onEditClick(){
        disable(inputConfirm);
        isEdit = true;
        registerStep = EDIT_REGSTEP;
        addTextMessage(getString(R.string.edit), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditNameClick(){
        disable(inputEdit);
        registerStep = ADDNAME_REGSTEP;
        addTextMessage(getString(R.string.full_name), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditPhoneNumberClick(){
        disable(inputEdit);
        registerStep = PHONENO_REGSTEP;
        addTextMessage(getString(R.string.phone_number), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditBirthClick(){
        disable(inputEdit);
        registerStep = BIRTHDAY_REGSTEP;
        addTextMessage(getString(R.string.hint_birthday), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditIdCardClick(){
        disable(inputEdit);
        registerStep = IDCARD_REGSTEP;
        addTextMessage(getString(R.string.identity_number), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditMGMClick(){
        disable(inputEdit);
        registerStep = MGM_REGSTEP;
        addTextMessage(mCaptionMGM, User.getUser());
        expandableLayout.collapse();
    }

    private void onEditIdCardImageClick(){
        disable(inputEdit);
        registerStep = IMAGE_REGSTEP;
        addTextMessage(getString(R.string.chat_id_card_image), User.getUser());
        expandableLayout.collapse();
    }

    private void confirmEditClick(){
        disable(inputConfirm);
        registerStep = ACCEPT_REGSTEP;
        addTextMessageNotDelay("ยืนยัน", User.getUser());
        addTextMessageWithLink(String.format("คุณ%s %s\n%s",firstName, getString(R.string.chat_accept_link), LINK), User.getTermTemUser());
        expandableLayout.collapse();
    }

    private void acceptRegister(){
        disable(inputAccept);
        expandableLayout.collapse();
        addTextMessageNotDelay("ขอเวลาเติมเต็มดำเนินการสักครู่นะครับ", User.getTermTemUser());
        getApiService().enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(response.body().getStatus() == APIServices.SUCCESS){
                    addTextMessageNotDelay("ขอบคุณครับคุณได้ลงทะเบียนกับน้องเติมเต็มเรียบร้อยครับ กรุณารอรับรหัสผู้ใช้งาน และ รหัสผ่าน เมื่อได้รับการอนุมัติจากบริษัท", User.getTermTemUser());
                    inputAccept.setVisibility(View.GONE);
                    inputTryAgain.setVisibility(View.GONE);
                    inputSuccess.setVisibility(View.VISIBLE);
                    enable(inputTryAgain);
                    enable(inputSuccess);
                    expandableLayout.expand();
                    clearData();
                }else {
                    if (response.body().getMsg().equalsIgnoreCase(getString(R.string.idcard_already_exists))){
                        addTextMessageNotDelay(String.format("เติมเต็มต้องขอโทษคุณ%s%s",
                                firstName, getString(R.string.cannot_register_duplicate_id_card)), User.getTermTemUser());
                    }else if (response.body().getMsg().equalsIgnoreCase(getString(R.string.phone_no_already_exists))){
                        addTextMessageNotDelay(String.format("เติมเต็มต้องขอโทษคุณ%s%s",
                                firstName, getString(R.string.cannot_register_duplicate_phone_number)), User.getTermTemUser());
                    }else {
                        addTextMessageNotDelay("ลงทะเบียนไม่สำเร็จ กรุณาลองใหม่อีกครั้ง", User.getTermTemUser());
                    }
                    addTextMessageNotDelay(String.format("คุณ%s ต้องการแก้ไขข้อมูลเพื่อลงทะเบียนใหม่หรือเปล่าครับ", firstName), User.getTermTemUser());
                    inputAccept.setVisibility(View.GONE);
                    inputSuccess.setVisibility(View.GONE);
                    inputTryAgain.setVisibility(View.VISIBLE);
                    enable(inputTryAgain);
                    expandableLayout.expand();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                addTextMessageNotDelay("ต้องขอโทษด้วยนะครับ ตอนนี้น้องเติมเต็มไม่สามารถเชื่อมต่อสัญญาณอินเตอเน็ตได้ โปรดตรวจสอบการเชื่อมต่อในโทรศัพท์ของท่านด้วยครับ", User.getTermTemUser());
                inputAccept.setVisibility(View.GONE);
                inputSuccess.setVisibility(View.GONE);
                inputTryAgain.setVisibility(View.VISIBLE);
                enable(inputTryAgain);
                expandableLayout.expand();
            }
        });
    }

    private void bankAccountClick(){
        disable(inputTransfer);
        transferStep = 1;
        addTextMessage("โอนเข้าบัญชีธนาคาร", User.getUser());
        expandableLayout.collapse();
    }

    private void mPayClick(){
        disable(inputEdit);
        transferStep = 2;
        addTextMessage("โอนผ่านเอ็มเปย์สเตชั่น", User.getUser());
        expandableLayout.collapse();
    }

    private void mPayStationClick(){
        disable(inputMpay);
        transferStep = 3;
        addTextMessage("ผ่านจุดบริการเอ็มเปย์สเตชั่น", User.getUser());
        expandableLayout.collapse();
    }

    private void atmClick(){
        disable(inputMpay);
        transferStep = 4;
        addTextMessage("ผ่านตู้ ATM", User.getUser());
        expandableLayout.collapse();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_IMAGE_CAPTURE:
                    imgPath = Util.getRealPathFromURI(photoURI);
                    Glide.with(getContext()).load(imgPath).into(idCardImage);
                    break;
            }
        }
    }

    private Call<ResponseModel> getApiService(){
        System.gc();
        Glide.clear(idCardImage);
        Bitmap bitmapImage = Util.flip(Util.decodeSampledBitmapFromResource(imgPath, 300, 300), imgPath);
        String image = Util.encodeBitmapToUpload(bitmapImage);
        if (bitmapImage != null) {
            bitmapImage.recycle();
        }

        RegisterRequestModel.Data data = new RegisterRequestModel.Data(
                prefixName,
                firstName,
                lastName,
                calendar.getTimeInMillis(),
                "chat",
                phoneNumber,
                idCard,
                0,
                image
        );

        data.setREFCODE(phoneMGM);

        Call<ResponseModel> call = service.SIGNUP(new RegisterRequestModel(data));
        return call;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void clearData(){
        isEdit = false;
        firstName = "";
        lastName = "";
        prefixName = "";
        groupPrefixName.clearCheck();
        calendar = Calendar.getInstance();
        setupCalendar();
        edtIdCard.setText("");
        edtPhoneNumber.setText("");
        edtLastName.setText("");
        edtName.setText("");
        edtBirth.setText("");
        imgPath = "";
        idCardImage.setImageResource(0);
    }

    private void createDialog(){
        dialogWebView = new Dialog(this, R.style.DialogFullScreen);
        dialogWebView.setContentView(R.layout.dialog_policy);
        dialogWebView.getWindow().setBackgroundDrawableResource(R.color.grayDarkTransparent);

        webView = (WebView) dialogWebView.findViewById(R.id.webView_policy);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private static void disable(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    private static void enable(ViewGroup layout) {
        layout.setEnabled(true);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enable((ViewGroup) child);
            } else {
                child.setEnabled(true);
            }
        }
    }
}