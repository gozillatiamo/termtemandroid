package com.worldwidewealth.termtem.chat.holder;

import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.chat.model.Message;

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private View onlineIndicator;

    public CustomIncomingTextMessageViewHolder(View itemView) {
        super(itemView);
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
    }
}
