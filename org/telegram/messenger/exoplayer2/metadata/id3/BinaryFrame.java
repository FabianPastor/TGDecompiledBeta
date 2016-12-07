package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class BinaryFrame extends Id3Frame {
    public static final Creator<BinaryFrame> CREATOR = new Creator<BinaryFrame>() {
        public BinaryFrame createFromParcel(Parcel in) {
            return new BinaryFrame(in);
        }

        public BinaryFrame[] newArray(int size) {
            return new BinaryFrame[size];
        }
    };
    public final byte[] data;

    public BinaryFrame(String id, byte[] data) {
        super(id);
        this.data = data;
    }

    BinaryFrame(Parcel in) {
        super(in.readString());
        this.data = in.createByteArray();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BinaryFrame other = (BinaryFrame) obj;
        if (this.id.equals(other.id) && Arrays.equals(this.data, other.data)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((this.id.hashCode() + 527) * 31) + Arrays.hashCode(this.data);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByteArray(this.data);
    }
}
