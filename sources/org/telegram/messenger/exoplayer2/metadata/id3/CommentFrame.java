package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CommentFrame extends Id3Frame {
    public static final Creator<CommentFrame> CREATOR = new C05831();
    public static final String ID = "COMM";
    public final String description;
    public final String language;
    public final String text;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame$1 */
    static class C05831 implements Creator<CommentFrame> {
        C05831() {
        }

        public CommentFrame createFromParcel(Parcel parcel) {
            return new CommentFrame(parcel);
        }

        public CommentFrame[] newArray(int i) {
            return new CommentFrame[i];
        }
    }

    public CommentFrame(String str, String str2, String str3) {
        super(ID);
        this.language = str;
        this.description = str2;
        this.text = str3;
    }

    CommentFrame(Parcel parcel) {
        super(ID);
        this.language = parcel.readString();
        this.description = parcel.readString();
        this.text = parcel.readString();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                CommentFrame commentFrame = (CommentFrame) obj;
                if (!Util.areEqual(this.description, commentFrame.description) || !Util.areEqual(this.language, commentFrame.language) || Util.areEqual(this.text, commentFrame.text) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = 31 * (((527 + (this.language != null ? this.language.hashCode() : 0)) * 31) + (this.description != null ? this.description.hashCode() : 0));
        if (this.text != null) {
            i = this.text.hashCode();
        }
        return hashCode + i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.language);
        parcel.writeString(this.text);
    }
}
