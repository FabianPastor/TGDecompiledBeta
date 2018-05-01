package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class RepresentationKey implements Parcelable, Comparable<RepresentationKey> {
    public static final Creator<RepresentationKey> CREATOR = new C06131();
    public final int adaptationSetIndex;
    public final int periodIndex;
    public final int representationIndex;

    /* renamed from: org.telegram.messenger.exoplayer2.source.dash.manifest.RepresentationKey$1 */
    static class C06131 implements Creator<RepresentationKey> {
        C06131() {
        }

        public RepresentationKey createFromParcel(Parcel parcel) {
            return new RepresentationKey(parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        public RepresentationKey[] newArray(int i) {
            return new RepresentationKey[i];
        }
    }

    public int describeContents() {
        return 0;
    }

    public RepresentationKey(int i, int i2, int i3) {
        this.periodIndex = i;
        this.adaptationSetIndex = i2;
        this.representationIndex = i3;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.periodIndex);
        stringBuilder.append(".");
        stringBuilder.append(this.adaptationSetIndex);
        stringBuilder.append(".");
        stringBuilder.append(this.representationIndex);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.periodIndex);
        parcel.writeInt(this.adaptationSetIndex);
        parcel.writeInt(this.representationIndex);
    }

    public int compareTo(RepresentationKey representationKey) {
        int i = this.periodIndex - representationKey.periodIndex;
        if (i != 0) {
            return i;
        }
        i = this.adaptationSetIndex - representationKey.adaptationSetIndex;
        return i == 0 ? this.representationIndex - representationKey.representationIndex : i;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                RepresentationKey representationKey = (RepresentationKey) obj;
                if (this.periodIndex != representationKey.periodIndex || this.adaptationSetIndex != representationKey.adaptationSetIndex || this.representationIndex != representationKey.representationIndex) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((this.periodIndex * 31) + this.adaptationSetIndex)) + this.representationIndex;
    }
}
