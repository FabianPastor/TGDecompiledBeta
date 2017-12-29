package net.hockeyapp.android.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Feedback implements Serializable {
    private String mCreatedAt;
    private String mEmail;
    private int mId;
    private ArrayList<FeedbackMessage> mMessages;
    private String mName;

    public void setName(String name) {
        this.mName = name;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public ArrayList<FeedbackMessage> getMessages() {
        return this.mMessages;
    }

    public void setMessages(ArrayList<FeedbackMessage> messages) {
        this.mMessages = messages;
    }
}
