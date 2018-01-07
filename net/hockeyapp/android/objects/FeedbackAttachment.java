package net.hockeyapp.android.objects;

import java.io.Serializable;

public class FeedbackAttachment implements Serializable {
    private String mCreatedAt;
    private String mFilename;
    private int mId;
    private int mMessageId;
    private String mUpdatedAt;
    private String mUrl;

    public void setId(int id) {
        this.mId = id;
    }

    public void setMessageId(int messageId) {
        this.mMessageId = messageId;
    }

    public String getFilename() {
        return this.mFilename;
    }

    public void setFilename(String filename) {
        this.mFilename = filename;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.mUpdatedAt = updatedAt;
    }

    public String getCacheId() {
        return TtmlNode.ANONYMOUS_REGION_ID + this.mMessageId + this.mId;
    }

    public String toString() {
        return "\n" + FeedbackAttachment.class.getSimpleName() + "\nid         " + this.mId + "\nmessage id " + this.mMessageId + "\nfilename   " + this.mFilename + "\nurl        " + this.mUrl + "\ncreatedAt  " + this.mCreatedAt + "\nupdatedAt  " + this.mUpdatedAt;
    }
}
