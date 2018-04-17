package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CommentFrame extends Id3Frame {
    public static final Creator<CommentFrame> CREATOR = new C05801();
    public static final String ID = "COMM";
    public final String description;
    public final String language;
    public final String text;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame$1 */
    static class C05801 implements Creator<CommentFrame> {
        C05801() {
        }

        public CommentFrame createFromParcel(Parcel in) {
            return new CommentFrame(in);
        }

        public CommentFrame[] newArray(int size) {
            return new CommentFrame[size];
        }
    }

    public CommentFrame(String language, String description, String text) {
        super(ID);
        this.language = language;
        this.description = description;
        this.text = text;
    }

    CommentFrame(Parcel in) {
        super(ID);
        this.language = in.readString();
        this.description = in.readString();
        this.text = in.readString();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                CommentFrame other = (CommentFrame) obj;
                if (!Util.areEqual(this.description, other.description) || !Util.areEqual(this.language, other.language) || !Util.areEqual(this.text, other.text)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int result = 31 * ((31 * ((31 * 17) + (this.language != null ? this.language.hashCode() : 0))) + (this.description != null ? this.description.hashCode() : 0));
        if (this.text != null) {
            i = this.text.hashCode();
        }
        return result + i;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.language);
        dest.writeString(this.text);
    }
}
