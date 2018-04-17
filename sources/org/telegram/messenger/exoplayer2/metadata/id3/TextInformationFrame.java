package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TextInformationFrame extends Id3Frame {
    public static final Creator<TextInformationFrame> CREATOR = new C05831();
    public final String description;
    public final String value;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.TextInformationFrame$1 */
    static class C05831 implements Creator<TextInformationFrame> {
        C05831() {
        }

        public TextInformationFrame createFromParcel(Parcel in) {
            return new TextInformationFrame(in);
        }

        public TextInformationFrame[] newArray(int size) {
            return new TextInformationFrame[size];
        }
    }

    public TextInformationFrame(String id, String description, String value) {
        super(id);
        this.description = description;
        this.value = value;
    }

    TextInformationFrame(Parcel in) {
        super(in.readString());
        this.description = in.readString();
        this.value = in.readString();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                TextInformationFrame other = (TextInformationFrame) obj;
                if (!this.id.equals(other.id) || !Util.areEqual(this.description, other.description) || !Util.areEqual(this.value, other.value)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int result = 31 * ((31 * ((31 * 17) + this.id.hashCode())) + (this.description != null ? this.description.hashCode() : 0));
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return result + i;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.value);
    }
}
