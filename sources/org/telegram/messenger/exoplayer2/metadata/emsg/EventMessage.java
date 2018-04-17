package org.telegram.messenger.exoplayer2.metadata.emsg;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessage implements Entry {
    public static final Creator<EventMessage> CREATOR = new C05751();
    public final long durationMs;
    private int hashCode;
    public final long id;
    public final byte[] messageData;
    public final long presentationTimeUs;
    public final String schemeIdUri;
    public final String value;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage$1 */
    static class C05751 implements Creator<EventMessage> {
        C05751() {
        }

        public EventMessage createFromParcel(Parcel in) {
            return new EventMessage(in);
        }

        public EventMessage[] newArray(int size) {
            return new EventMessage[size];
        }
    }

    public EventMessage(String schemeIdUri, String value, long durationMs, long id, byte[] messageData, long presentationTimeUs) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.durationMs = durationMs;
        this.id = id;
        this.messageData = messageData;
        this.presentationTimeUs = presentationTimeUs;
    }

    EventMessage(Parcel in) {
        this.schemeIdUri = in.readString();
        this.value = in.readString();
        this.presentationTimeUs = in.readLong();
        this.durationMs = in.readLong();
        this.id = in.readLong();
        this.messageData = in.createByteArray();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int i = 0;
            int hashCode = 31 * ((31 * 17) + (this.schemeIdUri != null ? this.schemeIdUri.hashCode() : 0));
            if (this.value != null) {
                i = this.value.hashCode();
            }
            this.hashCode = (31 * ((31 * ((31 * ((31 * (hashCode + i)) + ((int) (this.presentationTimeUs ^ (this.presentationTimeUs >>> 32))))) + ((int) (this.durationMs ^ (this.durationMs >>> 32))))) + ((int) (this.id ^ (this.id >>> 32))))) + Arrays.hashCode(this.messageData);
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                EventMessage other = (EventMessage) obj;
                if (this.presentationTimeUs != other.presentationTimeUs || this.durationMs != other.durationMs || this.id != other.id || !Util.areEqual(this.schemeIdUri, other.schemeIdUri) || !Util.areEqual(this.value, other.value) || !Arrays.equals(this.messageData, other.messageData)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.schemeIdUri);
        dest.writeString(this.value);
        dest.writeLong(this.presentationTimeUs);
        dest.writeLong(this.durationMs);
        dest.writeLong(this.id);
        dest.writeByteArray(this.messageData);
    }
}
