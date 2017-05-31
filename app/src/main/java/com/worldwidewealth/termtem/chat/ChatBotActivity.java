package com.worldwidewealth.termtem.chat;

import android.app.DatePickerDialog;
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
import android.view.MotionEvent;
import android.view.View;
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
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.chat.holder.CustomIncomingImageMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomIncomingTextMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomOutcomingImageMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomOutcomingTextMessageViewHolder;
import com.worldwidewealth.termtem.chat.model.Message;
import com.worldwidewealth.termtem.chat.model.User;
import com.worldwidewealth.termtem.model.RegisterRequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.CheckSyntaxData;
import com.worldwidewealth.termtem.util.Util;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Santipong on 5/23/2017.
 */

public class ChatBotActivity extends MyAppcompatActivity implements
        DateFormatter.Formatter, View.OnClickListener, MessagesListAdapter.OnMessageClickListener<Message>{

    private static final long MESSAGE_DELAY_TIME = 1000;
    private static final int REMOVE_STEP = 99;
    private static final String LINK = "htttps://wwwealth.co/wealthservice/tc.html";
    private static final String IMG_BANK_ACCOUNT = "bank_account";
    private static final String IMG_MPAY = "mpay";
    private static final String IMG_ATM = "atm";
    private static final String IMG_TRANSFER = "transfer";

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

    //view first menu
    private View inputTutorial;
    private Button btnRegister;
    private Button btnBank;
    private Button btnAccount;
    private Button btnFinish;

    //View transfer
    private View inputTransfer;
    private View inputMpay;
    private Button btnMpay;
    private Button btnMpayStation;
    private Button btnBankAccount;
    private Button btnATM;

    //view register
    private View inputName;
    private View inputBirth;
    private View inputPhoneNumber;
    private View inputIdCard;
    private View inputImage;
    private View inputConfirm;
    private View inputEdit;
    private View inputAccept;
    private View inputSuccess;
    private View inputTryAgain;
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
    private Button btnEditIdcardImage;
    private Button btnEditCancel;
    private Button btnAccept;
    private Button btnNotAccept;
    private Button btnTryAgain;
    private Button btnEditTryAgain;
    private Button btnCancelTryAgain;
    private Button btnBye;
    private Button btnStay;

    private int step = 0;
    private int registerStep = 0;
    private int transferStep = 0;
    private boolean isEdit = false;

    //variable for register
    private String prefixName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String idCard;
    private Uri photoURI;
    private String imgPath;

    public static Intent create(Context context){
        return new Intent(context, ChatBotActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        handler = new Handler();
        bindView();
        setUpView();
        initAdapter();
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

        inputTutorial = (View) findViewById(R.id.input_tutorial);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnFinish = (Button) findViewById(R.id.btn_bye);
        btnAccount = (Button) findViewById(R.id.btn_transfer_account);
        btnBank = (Button) findViewById(R.id.btn_transfer_bank);

        inputName = (View) findViewById(R.id.input_register_name);
        inputBirth = (View) findViewById(R.id.input_birth);
        inputPhoneNumber = (View) findViewById(R.id.input_phone);
        inputIdCard = (View) findViewById(R.id.input_id_card);
        inputImage = (View) findViewById(R.id.input_image);
        inputConfirm = (View) findViewById(R.id.input_confirm_edit);
        inputEdit = (View) findViewById(R.id.input_edit);
        inputAccept = (View) findViewById(R.id.input_accept);
        inputTryAgain = (View) findViewById(R.id.input_try_again);
        inputSuccess = (View) findViewById(R.id.input_register_success);

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
        btnEditIdcardImage = (Button) findViewById(R.id.btn_edit_id_card_image);
        btnEditCancel = (Button) findViewById(R.id.btn_edit_cancel);
        btnAccept = (Button) findViewById(R.id.btn_accept);
        btnNotAccept = (Button) findViewById(R.id.btn_un_accept);
        btnTryAgain = (Button) findViewById(R.id.btn_try_again);
        btnEditTryAgain = (Button) findViewById(R.id.btn_edit_try_again);
        btnCancelTryAgain = (Button) findViewById(R.id.btn_cancel_try_again);
        btnBye = (Button) findViewById(R.id.btn_exit);
        btnStay = (Button) findViewById(R.id.btn_stay);

        inputTransfer = (View) findViewById(R.id.input_bank);
        inputMpay = (View) findViewById(R.id.input_mpay);

        btnMpay = (Button) findViewById(R.id.btn_mpay);
        btnMpayStation = (Button) findViewById(R.id.btn_mpay_station);
        btnBankAccount = (Button) findViewById(R.id.btn_bank);
        btnATM = (Button) findViewById(R.id.btn_atm);

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

        //set on button click
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register :
                if (isNetworkAvailable()) {
                    registerClick();
                }else {
                    addTextMessageNotDelay(String.format("เติมเต็มต้องขอโทษ%s",
                            getString(R.string.cannot_register_network_connection_problem)),
                            User.getTermTemUser());
                    expandableLayout.collapse();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputTutorial.setVisibility(View.GONE);
                            inputSuccess.setVisibility(View.VISIBLE);
                            expandableLayout.expand();
                        }
                    },1000);
                }
                break;
            case R.id.btn_transfer_bank :
                transferBankClick();
                break;
            case R.id.btn_transfer_account :
                transferAccountClick();
                break;
            case R.id.btn_bye :
                byeClick();
                break;
            case R.id.btn_cancel :
                cancelClick();
                break;
            case R.id.btn_confirm :
                confirmNameClick();
                break;
            case R.id.btn_confirm_birth :
                confirmBirthClick();
                break;
            case R.id.btn_cancel_birth :
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
                cancelClick();
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
                cancelClick();
                break;
            case R.id.btn_try_again:
                acceptRegister();
                break;
            case R.id.btn_edit_try_again:
                onEditClick();
                break;
            case R.id.btn_cancel_try_again:
                cancelClick();
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
        }
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
        }else if (message.getUrl()!=null){
            //show link
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
            case 0:
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
                inputTutorial.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 2:
                byeBye();
                break;
            case 3:
                step = 0;
                addImageMessage(IMG_TRANSFER, User.getTermTemUser());
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
            case 0:
                registerStep = 1;
                addTextMessage(getString(R.string.chat_register_tutorial), User.getTermTemUser());
                break;
            case 1:
                registerStep = 2;
                addTextMessage(getString(R.string.chat_add_name), User.getTermTemUser());
                break;
            case 2:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.VISIBLE);
                inputEdit.setVisibility(View.GONE);
                inputTutorial.setVisibility(View.GONE);
                expandableLayout.expand();
                break;
            case 3:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputBirth.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 4:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 5:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 6:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputImage.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 7:
                registerStep = 8;
                addTextMessage(String.format("%s%s %s\n%s : %s\n%s : %s\n%s : %s",prefixName, firstName, lastName,
                        getString(R.string.hint_birthday), edtBirth.getText().toString(),
                        getString(R.string.phone_number), phoneNumber,
                        getString(R.string.identity_number), idCard), User.getTermTemUser());
                break;
            case 8:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);

                addImageMessageNotDelay(imgPath, User.getTermTemUser());
                inputConfirm.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 9:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);
                inputConfirm.setVisibility(View.GONE);
                inputTryAgain.setVisibility(View.GONE);
                addTextMessage(String.format("คุณ%s ต้องการแก้ไขข้อมูลส่วนไหนครับ?", firstName), User.getTermTemUser());
                inputEdit.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 10:
                registerStep = REMOVE_STEP;
                inputName.setVisibility(View.GONE);
                inputBirth.setVisibility(View.GONE);
                inputPhoneNumber.setVisibility(View.GONE);
                inputIdCard.setVisibility(View.GONE);
                inputImage.setVisibility(View.GONE);
                inputConfirm.setVisibility(View.GONE);
                inputEdit.setVisibility(View.GONE);
                inputSuccess.setVisibility(View.GONE);
                inputAccept.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 11:

                break;
            case 12:

                break;
        }
    }

    private void handleTransferStep(){
        switch (transferStep){
            case 0:
                transferStep = REMOVE_STEP;
                addTextMessageNotDelay("คุณต้องการทราบวิธีโอนเงินเข้าบัญชีเติมเต็มวิธีไหนครับ?", User.getTermTemUser());
                inputTutorial.setVisibility(View.GONE);
                inputTransfer.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 1:
                step = 0;
                transferStep = 0;
                addImageMessageNotDelay(IMG_BANK_ACCOUNT, User.getTermTemUser());
                addTextMessage("เมื่อโอนเงินแล้วต้องแจ้งโอนเงินในโปรแกรมด้วยนะครับ", User.getTermTemUser());
                break;
            case 2:
                transferStep = REMOVE_STEP;
                inputTutorial.setVisibility(View.GONE);
                inputTransfer.setVisibility(View.GONE);
                inputMpay.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 3:
                step = 0;
                transferStep = 0;
                addImageMessage(IMG_MPAY, User.getTermTemUser());
                break;
            case 4:
                step = 0;
                transferStep = 0;
                addImageMessage(IMG_ATM, User.getTermTemUser());
                break;
        }
    }

   private void registerClick(){
       step = 5;
       registerStep = 0;
       addTextMessage(getString(R.string.chat_register_account), User.getUser());
       expandableLayout.collapse();
   }

   private void transferBankClick(){
       step = 3;
       addTextMessage(getString(R.string.chat_how_to_transfer_money_to_bank), User.getUser());
       expandableLayout.collapse();
   }

   private void transferAccountClick(){
       step = 4;
       addTextMessage(getString(R.string.chat_how_to_transfer_money_to_account), User.getUser());
       expandableLayout.collapse();
   }

   private void byeClick(){
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
       step = 0;
       addTextMessage(getString(R.string.cancel), User.getUser());
       expandableLayout.collapse();
       clearData();
   }

    private void cancelRegisterClick(){
        step = 0;
        addTextMessage("ยกเลิกการสมัคร", User.getUser());
        expandableLayout.collapse();
    }

    private void stayClick(){
        step = 0;
        addTextMessage("คุยต่อ", User.getUser());
        expandableLayout.collapse();
    }

   private void confirmNameClick(){
        if (prefixName == null || prefixName.equalsIgnoreCase("")){
            addTextMessage(getString(R.string.chat_please_select_prefix), User.getTermTemUser());
        }else if (edtName.getText().toString().trim().equals("")){
            addTextMessage(getString(R.string.chat_please_enter_name), User.getTermTemUser());
        }else if (edtLastName.getText().toString().trim().equals("")){
           addTextMessage(getString(R.string.chat_please_enter_last_name), User.getTermTemUser());
       }else {
            firstName = edtName.getText().toString().trim();
            lastName = edtLastName.getText().toString().trim();
            if (!isEdit) {
                registerStep = 3;
                addTextMessageNotDelay(String.format("%s%s %s", prefixName, firstName, lastName), User.getUser());
                addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_enter_birthday)), User.getTermTemUser());
                expandableLayout.collapse();
            }else {
                onEditSuccess();
            }
        }
   }

   private void confirmBirthClick(){
       if (edtBirth.getText().toString().trim().equals("")){
           addTextMessage(getString(R.string.chat_please_enter_birth), User.getTermTemUser());
       }else {
           if (!isEdit) {
               registerStep = 4;
               addTextMessageNotDelay(edtBirth.getText().toString(), User.getUser());
               addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_enter_phone_number)), User.getTermTemUser());
               expandableLayout.collapse();
           }else {
               onEditSuccess();
           }
       }
   }

    private void confirmPhoneClick(){
        if (edtPhoneNumber.getText().toString().trim().equals("")){
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_phone)), User.getTermTemUser());
        }else if (!CheckSyntaxData.isPhoneValid(edtPhoneNumber.getText().toString())){
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_phone_number_is_invalid)), User.getTermTemUser());
        }else {
            if (!isEdit) {
                registerStep = 5;
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
        if (edtIdCard.getText().toString().trim().equals("")){
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_id)), User.getTermTemUser());
        }else {
            if (edtIdCard.getText().toString().length()==13){
                if (!CheckSyntaxData.isIdentificationValid(edtIdCard.getText().toString())){
                    addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_id_card_is_invalid)), User.getTermTemUser());
                }else {
                    if (!isEdit) {
                        registerStep = 6;
                        idCard = edtIdCard.getText().toString();
                        addTextMessageNotDelay(idCard, User.getUser());
                        addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_please_select_image)), User.getTermTemUser());
                        expandableLayout.collapse();
                    }else {
                        idCard = edtIdCard.getText().toString();
                        onEditSuccess();
                    }
                }
            }else {
                addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_enter_id)), User.getTermTemUser());
            }
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
                    addTextMessage(getString(R.string.year_old_wrong), User.getTermTemUser());
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
        if (imgPath==null){
            addTextMessage(String.format("ขอโทษด้วยครับ คุณ%s %s",firstName, getString(R.string.chat_please_select_image)), User.getTermTemUser());
        }else {
            if (!isEdit) {
                registerStep = 7;
                addImageMessageNotDelay(imgPath, User.getUser());
                addTextMessage(String.format("คุณ%s %s", firstName, getString(R.string.chat_please_check)), User.getTermTemUser());
                expandableLayout.collapse();
            }else {
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
        registerStep = 7;
        addTextMessage(String.format("คุณ%s %s",firstName, getString(R.string.chat_please_check)), User.getTermTemUser());
        expandableLayout.collapse();
    }

    private void onEditClick(){
        isEdit = true;
        registerStep = 9;
        addTextMessage(getString(R.string.edit), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditNameClick(){
        registerStep = 2;
        addTextMessage(getString(R.string.full_name), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditPhoneNumberClick(){
        registerStep = 4;
        addTextMessage(getString(R.string.phone_number), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditBirthClick(){
        registerStep = 3;
        addTextMessage(getString(R.string.hint_birthday), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditIdCardClick(){
        registerStep = 5;
        addTextMessage(getString(R.string.identity_number), User.getUser());
        expandableLayout.collapse();
    }

    private void onEditIdCardImageClick(){
        registerStep = 6;
        addTextMessage(getString(R.string.chat_id_card_image), User.getUser());
        expandableLayout.collapse();
    }

    private void confirmEditClick(){
        registerStep = 10;
        addTextMessageNotDelay("ยืนยัน", User.getUser());
        addTextMessageWithLink(String.format("คุณ%s %s\n%s",firstName, getString(R.string.chat_accept_link), LINK), User.getUser());
        expandableLayout.collapse();
    }

    private void acceptRegister(){
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
                        addTextMessageNotDelay(response.body().getMsg(), User.getTermTemUser());
                    }
                    inputAccept.setVisibility(View.GONE);
                    inputSuccess.setVisibility(View.GONE);
                    inputTryAgain.setVisibility(View.VISIBLE);
                    expandableLayout.expand();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                addTextMessageNotDelay(String.format("เติมเต็มต้องขอโทษคุณ%s%s", firstName, getString(R.string.cannot_register_network_connection_problem)), User.getTermTemUser());
                inputAccept.setVisibility(View.GONE);
                inputSuccess.setVisibility(View.GONE);
                inputTryAgain.setVisibility(View.VISIBLE);
                expandableLayout.expand();
            }
        });
    }

    private void bankAccountClick(){
        transferStep = 1;
        addTextMessage("โอนเข้าบัญชีธนาคาร", User.getUser());
        expandableLayout.collapse();
    }

    private void mPayClick(){
        transferStep = 2;
        addTextMessage("โอนผ่านเอ็มเปย์สเตชั่น", User.getUser());
        expandableLayout.collapse();
    }

    private void mPayStationClick(){
        transferStep = 3;
        addTextMessage("ผ่านจุดบริการเอ็มเปย์สเตชั่น", User.getUser());
        expandableLayout.collapse();
    }

    private void atmClick(){
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
        Bitmap bitmapImage = Util.flip(Util.decodeSampledBitmapFromResource(imgPath, 300, 300), imgPath);
        return APIServices.retrofit.create(APIServices.class).SIGNUP(new RegisterRequestModel(new RegisterRequestModel.Data(
               prefixName,
                firstName,
                lastName,
                calendar.getTimeInMillis(),
                "",
                phoneNumber,
                idCard,
                0,
                Util.encodeBitmapToUpload(bitmapImage)
        )));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void clearData(){
        firstName = "";
        lastName = "";
        prefixName = "";
        edtIdCard.setText("");
        edtPhoneNumber.setText("");
        edtLastName.setText("");
        edtName.setText("");
        edtBirth.setText("");
        imgPath = "";
        idCardImage.setImageResource(0);
    }

}