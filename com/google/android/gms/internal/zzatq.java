package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatq extends zza {
    public static final Creator<zzatq> CREATOR = new zzatr();
    public final String name;
    public final String zzbqW;
    public final zzato zzbrH;
    public final long zzbrI;

    zzatq(zzatq com_google_android_gms_internal_zzatq, long j) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        this.name = com_google_android_gms_internal_zzatq.name;
        this.zzbrH = com_google_android_gms_internal_zzatq.zzbrH;
        this.zzbqW = com_google_android_gms_internal_zzatq.zzbqW;
        this.zzbrI = j;
    }

    public zzatq(String str, zzato com_google_android_gms_internal_zzato, String str2, long j) {
        this.name = str;
        this.zzbrH = com_google_android_gms_internal_zzato;
        this.zzbqW = str2;
        this.zzbrI = j;
    }

    public String toString() {
        String str = this.zzbqW;
        String str2 = this.name;
        String valueOf = String.valueOf(this.zzbrH);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzatr.zza(this, parcel, i);
    }
}
