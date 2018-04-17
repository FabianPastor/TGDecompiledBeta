package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ApicFrame extends Id3Frame {
    public static final Creator<ApicFrame> CREATOR = new C05761();
    public static final String ID = "APIC";
    public final String description;
    public final String mimeType;
    public final byte[] pictureData;
    public final int pictureType;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.ApicFrame$1 */
    static class C05761 implements Creator<ApicFrame> {
        C05761() {
        }

        public ApicFrame createFromParcel(Parcel in) {
            return new ApicFrame(in);
        }

        public ApicFrame[] newArray(int size) {
            return new ApicFrame[size];
        }
    }

    public ApicFrame(String mimeType, String description, int pictureType, byte[] pictureData) {
        super(ID);
        this.mimeType = mimeType;
        this.description = description;
        this.pictureType = pictureType;
        this.pictureData = pictureData;
    }

    ApicFrame(Parcel in) {
        super(ID);
        this.mimeType = in.readString();
        this.description = in.readString();
        this.pictureType = in.readInt();
        this.pictureData = in.createByteArray();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                ApicFrame other = (ApicFrame) obj;
                if (this.pictureType != other.pictureType || !Util.areEqual(this.mimeType, other.mimeType) || !Util.areEqual(this.description, other.description) || !Arrays.equals(this.pictureData, other.pictureData)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = 31 * ((31 * ((31 * 17) + this.pictureType)) + (this.mimeType != null ? this.mimeType.hashCode() : 0));
        if (this.description != null) {
            i = this.description.hashCode();
        }
        return (31 * (hashCode + i)) + Arrays.hashCode(this.pictureData);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeString(this.description);
        dest.writeInt(this.pictureType);
        dest.writeByteArray(this.pictureData);
    }
}
