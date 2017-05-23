package com.worldwidewealth.termtem.chat.holder;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.chat.model.Message;


public class CustomIncomingImageMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<Message> {

    private View onlineIndicator;

    public CustomIncomingImageMessageViewHolder(View itemView) {
        super(itemView);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
    }
}