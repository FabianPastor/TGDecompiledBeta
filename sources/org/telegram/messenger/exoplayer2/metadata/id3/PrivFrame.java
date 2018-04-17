package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class PrivFrame extends Id3Frame {
    public static final Creator<PrivFrame> CREATOR = new C05851();
    public static final String ID = "PRIV";
    public final String owner;
    public final byte[] privateData;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.PrivFrame$1 */
    static class C05851 implements Creator<PrivFrame> {
        C05851() {
        }

        public PrivFrame createFromParcel(Parcel in) {
            return new PrivFrame(in);
        }

        public PrivFrame[] newArray(int size) {
            return new PrivFrame[size];
        }
    }

    public PrivFrame(String owner, byte[] privateData) {
        super(ID);
        this.owner = owner;
        this.privateData = privateData;
    }

    PrivFrame(Parcel in) {
        super(ID);
        this.owner = in.readString();
        this.privateData = in.createByteArray();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                PrivFrame other = (PrivFrame) obj;
                if (!Util.areEqual(this.owner, other.owner) || !Arrays.equals(this.privateData, other.privateData)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((31 * 17) + (this.owner != null ? this.owner.hashCode() : 0))) + Arrays.hashCode(this.privateData);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.owner);
        dest.writeByteArray(this.privateData);
    }
}
