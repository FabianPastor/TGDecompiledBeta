package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TextInformationFrame extends Id3Frame {
    public static final Creator<TextInformationFrame> CREATOR = new C05861();
    public final String description;
    public final String value;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.TextInformationFrame$1 */
    static class C05861 implements Creator<TextInformationFrame> {
        C05861() {
        }

        public TextInformationFrame createFromParcel(Parcel parcel) {
            return new TextInformationFrame(parcel);
        }

        public TextInformationFrame[] newArray(int i) {
            return new TextInformationFrame[i];
        }
    }

    public TextInformationFrame(String str, String str2, String str3) {
        super(str);
        this.description = str2;
        this.value = str3;
    }

    TextInformationFrame(Parcel parcel) {
        super(parcel.readString());
        this.description = parcel.readString();
        this.value = parcel.readString();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) obj;
                if (!this.id.equals(textInformationFrame.id) || !Util.areEqual(this.description, textInformationFrame.description) || Util.areEqual(this.value, textInformationFrame.value) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = 31 * (((527 + this.id.hashCode()) * 31) + (this.description != null ? this.description.hashCode() : 0));
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return hashCode + i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.description);
        parcel.writeString(this.value);
    }
}
