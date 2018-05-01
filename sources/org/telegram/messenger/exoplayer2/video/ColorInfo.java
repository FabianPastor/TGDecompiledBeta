package org.telegram.messenger.exoplayer2.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class ColorInfo implements Parcelable {
    public static final Creator<ColorInfo> CREATOR = new C06281();
    public final int colorRange;
    public final int colorSpace;
    public final int colorTransfer;
    private int hashCode;
    public final byte[] hdrStaticInfo;

    /* renamed from: org.telegram.messenger.exoplayer2.video.ColorInfo$1 */
    static class C06281 implements Creator<ColorInfo> {
        C06281() {
        }

        public ColorInfo createFromParcel(Parcel parcel) {
            return new ColorInfo(parcel);
        }

        public ColorInfo[] newArray(int i) {
            return new ColorInfo[0];
        }
    }

    public int describeContents() {
        return 0;
    }

    public ColorInfo(int i, int i2, int i3, byte[] bArr) {
        this.colorSpace = i;
        this.colorRange = i2;
        this.colorTransfer = i3;
        this.hdrStaticInfo = bArr;
    }

    ColorInfo(Parcel parcel) {
        this.colorSpace = parcel.readInt();
        this.colorRange = parcel.readInt();
        this.colorTransfer = parcel.readInt();
        this.hdrStaticInfo = (parcel.readInt() != 0 ? 1 : null) != null ? parcel.createByteArray() : null;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                ColorInfo colorInfo = (ColorInfo) obj;
                if (this.colorSpace != colorInfo.colorSpace || this.colorRange != colorInfo.colorRange || this.colorTransfer != colorInfo.colorTransfer || Arrays.equals(this.hdrStaticInfo, colorInfo.hdrStaticInfo) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ColorInfo(");
        stringBuilder.append(this.colorSpace);
        stringBuilder.append(", ");
        stringBuilder.append(this.colorRange);
        stringBuilder.append(", ");
        stringBuilder.append(this.colorTransfer);
        stringBuilder.append(", ");
        stringBuilder.append(this.hdrStaticInfo != null);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (31 * (((((527 + this.colorSpace) * 31) + this.colorRange) * 31) + this.colorTransfer)) + Arrays.hashCode(this.hdrStaticInfo);
        }
        return this.hashCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.colorSpace);
        parcel.writeInt(this.colorRange);
        parcel.writeInt(this.colorTransfer);
        parcel.writeInt(this.hdrStaticInfo != 0 ? 1 : 0);
        if (this.hdrStaticInfo != 0) {
            parcel.writeByteArray(this.hdrStaticInfo);
        }
    }
}
