package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class UrlLinkFrame extends Id3Frame {
    public static final Creator<UrlLinkFrame> CREATOR = new C05841();
    public final String description;
    public final String url;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.UrlLinkFrame$1 */
    static class C05841 implements Creator<UrlLinkFrame> {
        C05841() {
        }

        public UrlLinkFrame createFromParcel(Parcel in) {
            return new UrlLinkFrame(in);
        }

        public UrlLinkFrame[] newArray(int size) {
            return new UrlLinkFrame[size];
        }
    }

    public UrlLinkFrame(String id, String description, String url) {
        super(id);
        this.description = description;
        this.url = url;
    }

    UrlLinkFrame(Parcel in) {
        super(in.readString());
        this.description = in.readString();
        this.url = in.readString();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                UrlLinkFrame other = (UrlLinkFrame) obj;
                if (!this.id.equals(other.id) || !Util.areEqual(this.description, other.description) || !Util.areEqual(this.url, other.url)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int result = 31 * ((31 * ((31 * 17) + this.id.hashCode())) + (this.description != null ? this.description.hashCode() : 0));
        if (this.url != null) {
            i = this.url.hashCode();
        }
        return result + i;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
        dest.writeString(this.url);
    }
}
