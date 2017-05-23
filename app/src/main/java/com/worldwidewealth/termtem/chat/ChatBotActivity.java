package com.worldwidewealth.termtem.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
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

import java.util.Date;


/**
 * Created by Santipong on 5/23/2017.
 */

public class ChatBotActivity extends MyAppcompatActivity implements
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        DateFormatter.Formatter{

    private static final long MESSAGE_DELAY_TIME = 1000;

    private MessagesList messagesList;
    protected MessagesListAdapter<Message> messagesAdapter;
    private ImageLoader imageLoader;
    private Handler handler;
    private int i = 0;

    public static Intent create(Context context){
        return new Intent(context, ChatBotActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Glide.with(ChatBotActivity.this).load(url).asBitmap().into(imageView);
            }
        };

        handler = new Handler();
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
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
    public void onAddAttachments() {
        String url = "https://vignette2.wikia.nocookie.net/gintama/images/8/86/Gintama_Episode_52.png/revision/latest?cb=20130520105846";
        addImageMessage(url, User.getTermTemUser());
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        messagesAdapter.addToStart(Message.getTextMessage(User.getUser(), input.toString()), true);
        addTextMessage(input.toString(), User.getUser());
        return true;
    }

    @Override
    public String format(Date date) {
       return "";
    }

    private void addTextMessage(final String text, final User user){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (i){
                    case 0:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_hello_term_tem), User.getTermTemUser());
                        i++;
                        break;
                    case 1:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_register), User.getTermTemUser());
                        i++;
                        break;
                    case 2:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_news), User.getTermTemUser());
                        i++;
                        break;
                    case 3:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_manual), User.getTermTemUser());
                        i++;
                        break;
                    case 4:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_how_to), User.getTermTemUser());
                        i++;
                        break;
                    case 5:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_how_to), User.getTermTemUser());
                        i++;
                        break;
                    case 6:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_how_to), User.getTermTemUser());
                        i++;
                        break;
                    case 7:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_how_to), User.getTermTemUser());
                        i++;
                        break;
                    case 8:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_how_to), User.getTermTemUser());
                        i++;
                        break;
                    case 9:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        addTextMessage(getString(R.string.chat_how_to), User.getTermTemUser());
                        i++;
                        break;
                    case 10:
                        messagesAdapter.addToStart(Message.getTextMessage(user, text), true);
                        i++;
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
}
