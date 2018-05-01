package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class UrlLinkFrame extends Id3Frame {
    public static final Creator<UrlLinkFrame> CREATOR = new C05871();
    public final String description;
    public final String url;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.id3.UrlLinkFrame$1 */
    static class C05871 implements Creator<UrlLinkFrame> {
        C05871() {
        }

        public UrlLinkFrame createFromParcel(Parcel parcel) {
            return new UrlLinkFrame(parcel);
        }

        public UrlLinkFrame[] newArray(int i) {
            return new UrlLinkFrame[i];
        }
    }

    public UrlLinkFrame(String str, String str2, String str3) {
        super(str);
        this.description = str2;
        this.url = str3;
    }

    UrlLinkFrame(Parcel parcel) {
        super(parcel.readString());
        this.description = parcel.readString();
        this.url = parcel.readString();
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                UrlLinkFrame urlLinkFrame = (UrlLinkFrame) obj;
                if (!this.id.equals(urlLinkFrame.id) || !Util.areEqual(this.description, urlLinkFrame.description) || Util.areEqual(this.url, urlLinkFrame.url) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = 31 * (((527 + this.id.hashCode()) * 31) + (this.description != null ? this.description.hashCode() : 0));
        if (this.url != null) {
            i = this.url.hashCode();
        }
        return hashCode + i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.description);
        parcel.writeString(this.url);
    }
}
