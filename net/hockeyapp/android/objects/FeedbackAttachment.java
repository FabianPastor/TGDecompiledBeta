package net.hockeyapp.android.objects;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import net.hockeyapp.android.Constants;

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

    public boolean isAvailableInCache() {
        File folder = Constants.getHockeyAppStorageDir();
        if (!folder.exists() || !folder.isDirectory()) {
            return false;
        }
        File[] match = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.equals(FeedbackAttachment.this.getCacheId());
            }
        });
        if (match == null || match.length != 1) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "\n" + FeedbackAttachment.class.getSimpleName() + "\n" + "id         " + this.mId + "\n" + "message id " + this.mMessageId + "\n" + "filename   " + this.mFilename + "\n" + "url        " + this.mUrl + "\n" + "createdAt  " + this.mCreatedAt + "\n" + "updatedAt  " + this.mUpdatedAt;
    }
}
