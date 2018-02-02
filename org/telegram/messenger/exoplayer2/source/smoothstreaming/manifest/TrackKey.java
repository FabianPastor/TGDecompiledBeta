package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class TrackKey implements Parcelable, Comparable<TrackKey> {
    public static final Creator<TrackKey> CREATOR = new Creator<TrackKey>() {
        public TrackKey createFromParcel(Parcel in) {
            return new TrackKey(in.readInt(), in.readInt());
        }

        public TrackKey[] newArray(int size) {
            return new TrackKey[size];
        }
    };
    public final int streamElementIndex;
    public final int trackIndex;

    public TrackKey(int streamElementIndex, int trackIndex) {
        this.streamElementIndex = streamElementIndex;
        this.trackIndex = trackIndex;
    }

    public String toString() {
        return this.streamElementIndex + "." + this.trackIndex;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.streamElementIndex);
        dest.writeInt(this.trackIndex);
    }

    public int compareTo(TrackKey o) {
        int result = this.streamElementIndex - o.streamElementIndex;
        if (result == 0) {
            return this.trackIndex - o.trackIndex;
        }
        return result;
    }
}
