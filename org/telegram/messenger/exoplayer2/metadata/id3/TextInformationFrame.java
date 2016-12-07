package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TextInformationFrame extends Id3Frame {
    public static final Creator<TextInformationFrame> CREATOR = new Creator<TextInformationFrame>() {
        public TextInformationFrame createFromParcel(Parcel in) {
            return new TextInformationFrame(in);
        }

        public TextInformationFrame[] newArray(int size) {
            return new TextInformationFrame[size];
        }
    };
    public final String description;

    public TextInformationFrame(String id, String description) {
        super(id);
        this.description = description;
    }

    TextInformationFrame(Parcel in) {
        super(in.readString());
        this.description = in.readString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TextInformationFrame other = (TextInformationFrame) obj;
        if (this.id.equals(other.id) && Util.areEqual(this.description, other.description)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.id.hashCode() + 527) * 31) + (this.description != null ? this.description.hashCode() : 0);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
    }
}
