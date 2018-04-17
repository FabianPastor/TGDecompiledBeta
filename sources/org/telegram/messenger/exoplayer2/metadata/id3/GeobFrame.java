package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class GeobFrame extends Id3Frame {
    public static final Creator<GeobFrame> CREATOR = new C05811();
    public static final String ID = "GEOB";
    public final byte[] data;
    public final String description;
    public final String filename;
    public final String mimeType;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.GeobFrame$1 */
    static class C05811 implements Creator<GeobFrame> {
        C05811() {
        }

        public GeobFrame createFromParcel(Parcel in) {
            return new GeobFrame(in);
        }

        public GeobFrame[] newArray(int size) {
            return new GeobFrame[size];
        }
    }

    public GeobFrame(String mimeType, String filename, String description, byte[] data) {
        super(ID);
        this.mimeType = mimeType;
        this.filename = filename;
        this.description = description;
        this.data = data;
    }

    GeobFrame(Parcel in) {
        super(ID);
        this.mimeType = in.readString();
        this.filename = in.readString();
        this.description = in.readString();
        this.data = in.createByteArray();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                GeobFrame other = (GeobFrame) obj;
                if (!Util.areEqual(this.mimeType, other.mimeType) || !Util.areEqual(this.filename, other.filename) || !Util.areEqual(this.description, other.description) || !Arrays.equals(this.data, other.data)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = 31 * ((31 * ((31 * 17) + (this.mimeType != null ? this.mimeType.hashCode() : 0))) + (this.filename != null ? this.filename.hashCode() : 0));
        if (this.description != null) {
            i = this.description.hashCode();
        }
        return (31 * (hashCode + i)) + Arrays.hashCode(this.data);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mimeType);
        dest.writeString(this.filename);
        dest.writeString(this.description);
        dest.writeByteArray(this.data);
    }
}
