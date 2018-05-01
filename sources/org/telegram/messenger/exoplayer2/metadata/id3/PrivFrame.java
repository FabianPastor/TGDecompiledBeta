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

        public PrivFrame createFromParcel(Parcel parcel) {
            return new PrivFrame(parcel);
        }

        public PrivFrame[] newArray(int i) {
            return new PrivFrame[i];
        }
    }

    public PrivFrame(String str, byte[] bArr) {
        super(ID);
        this.owner = str;
        this.privateData = bArr;
    }

    PrivFrame(Parcel parcel) {
        super(ID);
        this.owner = parcel.readString();
        this.privateData = parcel.createByteArray();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                PrivFrame privFrame = (PrivFrame) obj;
                if (!Util.areEqual(this.owner, privFrame.owner) || Arrays.equals(this.privateData, privFrame.privateData) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * (527 + (this.owner != null ? this.owner.hashCode() : 0))) + Arrays.hashCode(this.privateData);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.owner);
        parcel.writeByteArray(this.privateData);
    }
}
