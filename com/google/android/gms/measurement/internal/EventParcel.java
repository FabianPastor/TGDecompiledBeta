package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public final class EventParcel extends AbstractSafeParcelable {
    public static final Creator<EventParcel> CREATOR = new zzk();
    public final EventParams arJ;
    public final String arK;
    public final long arL;
    public final String name;
    public final int versionCode;

    EventParcel(int i, String str, EventParams eventParams, String str2, long j) {
        this.versionCode = i;
        this.name = str;
        this.arJ = eventParams;
        this.arK = str2;
        this.arL = j;
    }

    public EventParcel(String str, EventParams eventParams, String str2, long j) {
        this.versionCode = 1;
        this.name = str;
        this.arJ = eventParams;
        this.arK = str2;
        this.arL = j;
    }

    public String toString() {
        String str = this.arK;
        String str2 = this.name;
        String valueOf = String.valueOf(this.arJ);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }
}
