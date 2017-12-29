package net.hockeyapp.android.objects;

import java.io.Serializable;
import java.util.List;

public class FeedbackMessage implements Serializable {
    private String mAppId;
    private String mCleanText;
    private String mCreatedAt;
    private String mDeviceModel;
    private String mDeviceOem;
    private String mDeviceOsVersion;
    private List<FeedbackAttachment> mFeedbackAttachments;
    private int mId;
    private String mName;
    private String mSubject;
    private String mText;
    private String mToken;
    private String mUserString;
    private int mVia;

    @Deprecated
    public void setSubjec(String subjec) {
        this.mSubject = subjec;
    }

    public String getText() {
        return this.mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public void setOem(String oem) {
        this.mDeviceOem = oem;
    }

    public void setModel(String model) {
        this.mDeviceModel = model;
    }

    public void setOsVersion(String osVersion) {
        this.mDeviceOsVersion = osVersion;
    }

    public String getCreatedAt() {
        return this.mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public void setVia(int via) {
        this.mVia = via;
    }

    public void setUserString(String userString) {
        this.mUserString = userString;
    }

    public void setCleanText(String cleanText) {
        this.mCleanText = cleanText;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setAppId(String appId) {
        this.mAppId = appId;
    }

    public List<FeedbackAttachment> getFeedbackAttachments() {
        return this.mFeedbackAttachments;
    }

    public void setFeedbackAttachments(List<FeedbackAttachment> feedbackAttachments) {
        this.mFeedbackAttachments = feedbackAttachments;
    }
}
