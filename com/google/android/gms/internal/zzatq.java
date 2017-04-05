package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class zzatq extends zza {
    public static final Creator<zzatq> CREATOR = new zzatr();
    public final String name;
    public final String zzbqV;
    public final zzato zzbrG;
    public final long zzbrH;

    zzatq(zzatq com_google_android_gms_internal_zzatq, long j) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        this.name = com_google_android_gms_internal_zzatq.name;
        this.zzbrG = com_google_android_gms_internal_zzatq.zzbrG;
        this.zzbqV = com_google_android_gms_internal_zzatq.zzbqV;
        this.zzbrH = j;
    }

    public zzatq(String str, zzato com_google_android_gms_internal_zzato, String str2, long j) {
        this.name = str;
        this.zzbrG = com_google_android_gms_internal_zzato;
        this.zzbqV = str2;
        this.zzbrH = j;
    }

    public String toString() {
        String str = this.zzbqV;
        String str2 = this.name;
        String valueOf = String.valueOf(this.zzbrG);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzatr.zza(this, parcel, i);
    }
}
