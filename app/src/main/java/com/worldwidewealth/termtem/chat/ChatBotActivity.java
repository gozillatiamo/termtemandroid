package com.worldwidewealth.termtem.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.chat.holder.CustomIncomingImageMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomIncomingTextMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomOutcomingImageMessageViewHolder;
import com.worldwidewealth.termtem.chat.holder.CustomOutcomingTextMessageViewHolder;
import com.worldwidewealth.termtem.chat.model.Message;
import com.worldwidewealth.termtem.chat.model.User;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Date;


/**
 * Created by Santipong on 5/23/2017.
 */

public class ChatBotActivity extends MyAppcompatActivity implements
        DateFormatter.Formatter, View.OnClickListener{

    private static final long MESSAGE_DELAY_TIME = 1000;
    private static final int REMOVE_STEP = 99;

    private MessagesList messagesList;
    protected MessagesListAdapter<Message> messagesAdapter;
    private ImageLoader imageLoader;
    private Handler handler;
    private ImageView typingLeft;
    private ImageView typingRight;
    private ExpandableLayout expandableLayout;
    private View inputTutorial;
    private View inputConfirm;
    private View inputRegister;
    private Button btnRegister;
    private Button btnTopUp;
    private Button btnFAQ;
    private Button btnNews;
    private Button btnManual;
    private Button btnFinish;
    private Button btnYes;
    private Button btnNo;
    private Button btnRegisterTutorial;
    private Button btnRegisterNow;

    private int step = 0;
    private int registerStep = 0;

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
        inputTutorial = (View) findViewById(R.id.input_tutorial);
        inputConfirm = (View) findViewById(R.id.input_confirm);
        inputRegister = (View) findViewById(R.id.input_register);

        //button tutorials
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnTopUp = (Button) findViewById(R.id.btn_top_up);
        btnFAQ = (Button) findViewById(R.id.btn_faq);
        btnNews = (Button) findViewById(R.id.btn_news);
        btnManual = (Button) findViewById(R.id.btn_manual);
        btnFinish = (Button) findViewById(R.id.btn_bye);

        //button confirm
        btnYes = (Button) findViewById(R.id.btn_yes);
        btnNo = (Button) findViewById(R.id.btn_no);

        //button register
        btnRegisterNow = (Button) findViewById(R.id.btn_register_now);
        btnRegisterTutorial = (Button) findViewById(R.id.btn_register_tutorial);

    }

    private void setUpView(){
        //set image loader for load image in message list
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(ChatBotActivity.this).load(url).asBitmap().into(imageView);
            }
        };

        //set typing image
        Glide.with(this).load(R.raw.typing).asGif().into(typingLeft);
        Glide.with(this).load(R.raw.typing).asGif().into(typingRight);

        //set on button click
        btnRegister.setOnClickListener(this);
        btnTopUp.setOnClickListener(this);
        btnFAQ.setOnClickListener(this);
        btnNews.setOnClickListener(this);
        btnManual.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        btnRegisterNow.setOnClickListener(this);
        btnRegisterTutorial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register :
                registerClick();
                break;
            case R.id.btn_top_up :

                break;
            case R.id.btn_faq :

                break;
            case R.id.btn_news :

                break;
            case R.id.btn_manual :

                break;
            case R.id.btn_bye :
                onBackPressed();
                break;
            case R.id.btn_yes :
                confirmRegister();
                break;
            case R.id.btn_no :
                cancelRegister();
                break;
            case R.id.btn_register_tutorial :
                registerTutorial();
                break;
            case R.id.btn_register_now :
                registerNow();
                break;
        }
    }

    private void initAdapter(){
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(CustomIncomingTextMessageViewHolder.class, R.layout.item_custom_incoming_text_message)
                .setOutcomingTextConfig(CustomOutcomingTextMessageViewHolder.class, R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(CustomIncomingImageMessageViewHolder.class, R.layout.item_custom_incoming_image_message)
                .setOutcomingImageConfig(CustomOutcomingImageMessageViewHolder.class, R.layout.item_custom_outcoming_image_message);

        messagesAdapter = new MessagesListAdapter<>("002", holdersConfig, imageLoader);
        messagesAdapter.setDateHeadersFormatter(this);
        messagesList.setAdapter(messagesAdapter);
        addTextMessage(getString(R.string.chat_hello_term_tem), User.getTermTemUser());
    }

    @Override
    public String format(Date date) {
       return "";
    }

    private void addTextMessage(final String text, final User user){
        if (!user.isMe()) {
            typingLeft.setVisibility(View.VISIBLE);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                typingLeft.setVisibility(View.GONE);
                messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                switch (step) {
                    case 0:
                        inputTutorial.setVisibility(View.VISIBLE);
                        expandableLayout.expand();
                        break;
                    case 1:
                        handleRegister();
                        break;
                }

            }
        }, MESSAGE_DELAY_TIME);
    }

    private void addImageMessage(final String url, final User user){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesAdapter.addToStart(Message.getImageMessage(user, url), true);
            }
        }, MESSAGE_DELAY_TIME);
    }

    private void handleRegister(){
        switch (registerStep){
            case 0:
                registerStep = 1;
                addTextMessage(getString(R.string.chat_is_have_account), User.getTermTemUser());
                break;
            case 1:
                registerStep = REMOVE_STEP;
                inputTutorial.setVisibility(View.GONE);
                inputConfirm.setVisibility(View.VISIBLE);
                expandableLayout.expand();
                break;
            case 2:
                registerStep = 3;
                addTextMessage(getString(R.string.chat_thank_again), User.getTermTemUser());
                break;
            case 3:
                step = 0;
                registerStep = REMOVE_STEP;
                inputTutorial.setVisibility(View.VISIBLE);
                inputConfirm.setVisibility(View.GONE);
                expandableLayout.expand();
                break;
            case 4:
                registerStep = REMOVE_STEP;
                inputRegister.setVisibility(View.VISIBLE);
                inputConfirm.setVisibility(View.GONE);
                expandableLayout.expand();
                break;
            case 5:

                break;
            case 6:

                break;
        }
    }

    private void registerClick(){
        step = 1;
        registerStep = 0;
        expandableLayout.collapse();
        addTextMessage(getString(R.string.chat_register), User.getUser());
    }

    private void confirmRegister(){
        registerStep = 2;
        expandableLayout.collapse();
        addTextMessage(getString(R.string.chat_yes), User.getUser());
    }

    private void cancelRegister(){
        registerStep = 4;
        expandableLayout.collapse();
        addTextMessage(getString(R.string.chat_no), User.getUser());
    }

    private void registerTutorial(){
        registerStep = 5;
        expandableLayout.collapse();
        addTextMessage(getString(R.string.chat_how_to_register), User.getUser());
    }

    private void registerNow(){
        registerStep = 6;
        expandableLayout.collapse();
        addTextMessage(getString(R.string.chat_register_now), User.getUser());
    }

}