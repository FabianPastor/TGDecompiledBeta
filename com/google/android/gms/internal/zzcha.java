package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;

public final class zzcha extends zzbfm {
    public static final Creator<zzcha> CREATOR = new zzchb();
    public final String name;
    public final String zziyf;
    public final zzcgx zzizt;
    public final long zzizu;

    zzcha(zzcha com_google_android_gms_internal_zzcha, long j) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcha);
        this.name = com_google_android_gms_internal_zzcha.name;
        this.zzizt = com_google_android_gms_internal_zzcha.zzizt;
        this.zziyf = com_google_android_gms_internal_zzcha.zziyf;
        this.zzizu = j;
    }

    public zzcha(String str, zzcgx com_google_android_gms_internal_zzcgx, String str2, long j) {
        this.name = str;
        this.zzizt = com_google_android_gms_internal_zzcgx;
        this.zziyf = str2;
        this.zzizu = j;
    }

    public final String toString() {
        String str = this.zziyf;
        String str2 = this.name;
        String valueOf = String.valueOf(this.zzizt);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.name, false);
        zzbfp.zza(parcel, 3, this.zzizt, i, false);
        zzbfp.zza(parcel, 4, this.zziyf, false);
        zzbfp.zza(parcel, 5, this.zzizu);
        zzbfp.zzai(parcel, zze);
    }
}
