package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzcey extends zza {
    public static final Creator<zzcey> CREATOR = new zzcez();
    public final String name;
    public final zzcev zzbpM;
    public final long zzbpN;
    public final String zzbpc;

    zzcey(zzcey com_google_android_gms_internal_zzcey, long j) {
        zzbo.zzu(com_google_android_gms_internal_zzcey);
        this.name = com_google_android_gms_internal_zzcey.name;
        this.zzbpM = com_google_android_gms_internal_zzcey.zzbpM;
        this.zzbpc = com_google_android_gms_internal_zzcey.zzbpc;
        this.zzbpN = j;
    }

    public zzcey(String str, zzcev com_google_android_gms_internal_zzcev, String str2, long j) {
        this.name = str;
        this.zzbpM = com_google_android_gms_internal_zzcev;
        this.zzbpc = str2;
        this.zzbpN = j;
    }

    public final String toString() {
        String str = this.zzbpc;
        String str2 = this.name;
        String valueOf = String.valueOf(this.zzbpM);
        return new StringBuilder(((String.valueOf(str).length() + 21) + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append("origin=").append(str).append(",name=").append(str2).append(",params=").append(valueOf).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.name, false);
        zzd.zza(parcel, 3, this.zzbpM, i, false);
        zzd.zza(parcel, 4, this.zzbpc, false);
        zzd.zza(parcel, 5, this.zzbpN);
        zzd.zzI(parcel, zze);
    }
}
