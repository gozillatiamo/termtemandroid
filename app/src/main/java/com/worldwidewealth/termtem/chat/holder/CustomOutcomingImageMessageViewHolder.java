package com.worldwidewealth.termtem.chat.holder;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.worldwidewealth.termtem.chat.model.Message;

public class CustomOutcomingImageMessageViewHolder
        extends MessageHolders.OutcomingImageMessageViewHolder<Message> {

    public CustomOutcomingImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
    }
}