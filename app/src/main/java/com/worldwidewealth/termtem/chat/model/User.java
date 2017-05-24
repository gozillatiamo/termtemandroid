package com.worldwidewealth.termtem.chat.model;

import com.stfalcon.chatkit.commons.models.IUser;


public class User implements IUser {

    private String id;
    private String name;
    private String avatar;
    private boolean isMe;

    public User(String id, String name, String avatar, boolean isMe) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.isMe = isMe;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public boolean isMe() {
        return isMe;
    }

    public static User getTermTemUser(){
        return new User("001", "Term Tem", null, false);
    }

    public static User getUser(){
        return new User("002", "You", null, true);
    }

}
