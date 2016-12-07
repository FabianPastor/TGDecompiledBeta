package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class EventParcel extends AbstractSafeParcelable {
    public static final zzk CREATOR = new zzk();
    public final String aoA;
    public final long aoB;
    public final EventParams aoz;
    public final String name;
    public final int versionCode;

    EventParcel(int i, String str, EventParams eventParams, String str2, long j) {
        this.versionCode = i;
        this.name = str;
        this.aoz = eventParams;
        this.aoA = str2;
        this.aoB = j;
    }

    public EventParcel(String str, EventParams eventParams, String str2, long j) {
        this.versionCode = 1;
        this.name = str;
        this.aoz = eventParams;
        this.aoA = str2;
        this.aoB = j;
    }

    public String toString() {
        String str = this.aoA;
        String str2 = this.name;
        String valueOf = String.valueOf(this.aoz);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }
}
