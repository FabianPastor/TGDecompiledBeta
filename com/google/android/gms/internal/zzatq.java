package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatq extends zza {
    public static final Creator<zzatq> CREATOR = new zzatr();
    public final String name;
    public final String zzbqZ;
    public final zzato zzbrK;
    public final long zzbrL;

    zzatq(zzatq com_google_android_gms_internal_zzatq, long j) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        this.name = com_google_android_gms_internal_zzatq.name;
        this.zzbrK = com_google_android_gms_internal_zzatq.zzbrK;
        this.zzbqZ = com_google_android_gms_internal_zzatq.zzbqZ;
        this.zzbrL = j;
    }

    public zzatq(String str, zzato com_google_android_gms_internal_zzato, String str2, long j) {
        this.name = str;
        this.zzbrK = com_google_android_gms_internal_zzato;
        this.zzbqZ = str2;
        this.zzbrL = j;
    }

    public String toString() {
        String str = this.zzbqZ;
        String str2 = this.name;
        String valueOf = String.valueOf(this.zzbrK);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzatr.zza(this, parcel, i);
    }
}
