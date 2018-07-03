package org.telegram.messenger.exoplayer2.source;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TrackGroup implements Parcelable {
    public static final Creator<TrackGroup> CREATOR = new C06331();
    private final Format[] formats;
    private int hashCode;
    public final int length;

    /* renamed from: org.telegram.messenger.exoplayer2.source.TrackGroup$1 */
    static class C06331 implements Creator<TrackGroup> {
        C06331() {
        }

        public TrackGroup createFromParcel(Parcel in) {
            return new TrackGroup(in);
        }

        public TrackGroup[] newArray(int size) {
            return new TrackGroup[size];
        }
    }

    public TrackGroup(Format... formats) {
        Assertions.checkState(formats.length > 0);
        this.formats = formats;
        this.length = formats.length;
    }

    TrackGroup(Parcel in) {
        this.length = in.readInt();
        this.formats = new Format[this.length];
        for (int i = 0; i < this.length; i++) {
            this.formats[i] = (Format) in.readParcelable(Format.class.getClassLoader());
        }
    }

    public Format getFormat(int index) {
        return this.formats[index];
    }

    public int indexOf(Format format) {
        for (int i = 0; i < this.formats.length; i++) {
            if (format == this.formats[i]) {
                return i;
            }
        }
        return -1;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = Arrays.hashCode(this.formats) + 527;
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TrackGroup other = (TrackGroup) obj;
        if (this.length == other.length && Arrays.equals(this.formats, other.formats)) {
            return true;
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.length);
        for (int i = 0; i < this.length; i++) {
            dest.writeParcelable(this.formats[i], 0);
        }
    }
}
