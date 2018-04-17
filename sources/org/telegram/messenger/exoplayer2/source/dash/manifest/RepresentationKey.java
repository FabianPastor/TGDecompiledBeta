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

        public RepresentationKey createFromParcel(Parcel in) {
            return new RepresentationKey(in.readInt(), in.readInt(), in.readInt());
        }

        public RepresentationKey[] newArray(int size) {
            return new RepresentationKey[size];
        }
    }

    public RepresentationKey(int periodIndex, int adaptationSetIndex, int representationIndex) {
        this.periodIndex = periodIndex;
        this.adaptationSetIndex = adaptationSetIndex;
        this.representationIndex = representationIndex;
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.periodIndex);
        dest.writeInt(this.adaptationSetIndex);
        dest.writeInt(this.representationIndex);
    }

    public int compareTo(RepresentationKey o) {
        int result = this.periodIndex - o.periodIndex;
        if (result != 0) {
            return result;
        }
        result = this.adaptationSetIndex - o.adaptationSetIndex;
        if (result == 0) {
            return this.representationIndex - o.representationIndex;
        }
        return result;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (getClass() == o.getClass()) {
                RepresentationKey that = (RepresentationKey) o;
                if (this.periodIndex != that.periodIndex || this.adaptationSetIndex != that.adaptationSetIndex || this.representationIndex != that.representationIndex) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        return (31 * ((31 * this.periodIndex) + this.adaptationSetIndex)) + this.representationIndex;
    }
}
