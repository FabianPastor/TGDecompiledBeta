package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class RepresentationKey implements Parcelable, Comparable<RepresentationKey> {
    public static final Creator<RepresentationKey> CREATOR = new Creator<RepresentationKey>() {
        public RepresentationKey createFromParcel(Parcel in) {
            return new RepresentationKey(in.readInt(), in.readInt(), in.readInt());
        }

        public RepresentationKey[] newArray(int size) {
            return new RepresentationKey[size];
        }
    };
    public final int adaptationSetIndex;
    public final int periodIndex;
    public final int representationIndex;

    public RepresentationKey(int periodIndex, int adaptationSetIndex, int representationIndex) {
        this.periodIndex = periodIndex;
        this.adaptationSetIndex = adaptationSetIndex;
        this.representationIndex = representationIndex;
    }

    public String toString() {
        return this.periodIndex + "." + this.adaptationSetIndex + "." + this.representationIndex;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepresentationKey that = (RepresentationKey) o;
        if (this.periodIndex == that.periodIndex && this.adaptationSetIndex == that.adaptationSetIndex && this.representationIndex == that.representationIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((this.periodIndex * 31) + this.adaptationSetIndex) * 31) + this.representationIndex;
    }
}
