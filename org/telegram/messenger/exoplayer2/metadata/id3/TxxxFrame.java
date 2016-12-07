package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TxxxFrame extends Id3Frame {
    public static final Creator<TxxxFrame> CREATOR = new Creator<TxxxFrame>() {
        public TxxxFrame createFromParcel(Parcel in) {
            return new TxxxFrame(in);
        }

        public TxxxFrame[] newArray(int size) {
            return new TxxxFrame[size];
        }
    };
    public static final String ID = "TXXX";
    public final String description;
    public final String value;

    public TxxxFrame(String description, String value) {
        super(ID);
        this.description = description;
        this.value = value;
    }

    TxxxFrame(Parcel in) {
        super(ID);
        this.description = in.readString();
        this.value = in.readString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TxxxFrame other = (TxxxFrame) obj;
        if (Util.areEqual(this.description, other.description) && Util.areEqual(this.value, other.value)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        if (this.description != null) {
            hashCode = this.description.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode + 527) * 31;
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return hashCode + i;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.value);
    }
}
