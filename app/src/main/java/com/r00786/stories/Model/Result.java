
package com.r00786.stories.Model;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Result {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("nowwt")
    private String text;
    @SerializedName("photo")
    private String mPhoto;
    @SerializedName("time")
    private String mTime;
    @SerializedName("type")
    private String type;
    @SerializedName("username")
    private String mUsername;

    public boolean isDurationSet() {
        return durationSet;
    }

    public void setDurationSet(boolean durationSet) {
        this.durationSet = durationSet;
    }

    private boolean durationSet;

    private boolean replyKbVisibilty;

    public boolean isReplyKbVisibilty() {
        return replyKbVisibilty;
    }

    public void setReplyKbVisibilty(boolean replyKbVisibilty) {
        this.replyKbVisibilty = replyKbVisibilty;
    }

    transient SimpleExoPlayer simpleExoPlayer;
    transient Player.DefaultEventListener eventListener;
    private transient Long duration;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Player.DefaultEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(Player.DefaultEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public SimpleExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;
    }

    public void setSimpleExoPlayer(SimpleExoPlayer simpleExoPlayer) {
        this.simpleExoPlayer = simpleExoPlayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean storyLoaded;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String nowwt) {
        text = nowwt;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

}
