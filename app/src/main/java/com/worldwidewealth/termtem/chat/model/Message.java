package com.worldwidewealth.termtem.chat.model;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;
import java.util.UUID;


public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private Image image;
    private String url;

    public Message(String id, User user, String text, String url) {
        this(id, user, text, new Date(), url);
    }

    public Message(String id, User user, String text, Date createdAt, String url) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.url = url;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public String getStatus() {
        return "Sent";
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }


    public static Message getTextMessage(User user, String text) {
        return new Message(getRandomId(), user, text, "");
    }

    public static Message getTextMessageWithUrl(User user, String text, String url) {
        return new Message(getRandomId(), user, text, url);
    }

    public static Message getImageMessage(User user, String url) {
        Message message = new Message(getRandomId(), user, null, "");
        message.setImage(new Message.Image(url));
        return message;
    }

    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }
}
