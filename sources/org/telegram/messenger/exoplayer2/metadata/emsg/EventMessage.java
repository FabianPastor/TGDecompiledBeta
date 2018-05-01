package org.telegram.messenger.exoplayer2.metadata.emsg;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessage implements Entry {
    public static final Creator<EventMessage> CREATOR = new C05781();
    public final long durationMs;
    private int hashCode;
    public final long id;
    public final byte[] messageData;
    public final long presentationTimeUs;
    public final String schemeIdUri;
    public final String value;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage$1 */
    static class C05781 implements Creator<EventMessage> {
        C05781() {
        }

        public EventMessage createFromParcel(Parcel parcel) {
            return new EventMessage(parcel);
        }

        public EventMessage[] newArray(int i) {
            return new EventMessage[i];
        }
    }

    public int describeContents() {
        return 0;
    }

    public EventMessage(String str, String str2, long j, long j2, byte[] bArr, long j3) {
        this.schemeIdUri = str;
        this.value = str2;
        this.durationMs = j;
        this.id = j2;
        this.messageData = bArr;
        this.presentationTimeUs = j3;
    }

    EventMessage(Parcel parcel) {
        this.schemeIdUri = parcel.readString();
        this.value = parcel.readString();
        this.presentationTimeUs = parcel.readLong();
        this.durationMs = parcel.readLong();
        this.id = parcel.readLong();
        this.messageData = parcel.createByteArray();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int i = 0;
            int hashCode = (527 + (this.schemeIdUri != null ? this.schemeIdUri.hashCode() : 0)) * 31;
            if (this.value != null) {
                i = this.value.hashCode();
            }
            this.hashCode = (31 * (((((((hashCode + i) * 31) + ((int) (this.presentationTimeUs ^ (this.presentationTimeUs >>> 32)))) * 31) + ((int) (this.durationMs ^ (this.durationMs >>> 32)))) * 31) + ((int) (this.id ^ (this.id >>> 32))))) + Arrays.hashCode(this.messageData);
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
                EventMessage eventMessage = (EventMessage) obj;
                if (this.presentationTimeUs != eventMessage.presentationTimeUs || this.durationMs != eventMessage.durationMs || this.id != eventMessage.id || !Util.areEqual(this.schemeIdUri, eventMessage.schemeIdUri) || !Util.areEqual(this.value, eventMessage.value) || Arrays.equals(this.messageData, eventMessage.messageData) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.schemeIdUri);
        parcel.writeString(this.value);
        parcel.writeLong(this.presentationTimeUs);
        parcel.writeLong(this.durationMs);
        parcel.writeLong(this.id);
        parcel.writeByteArray(this.messageData);
    }
}
