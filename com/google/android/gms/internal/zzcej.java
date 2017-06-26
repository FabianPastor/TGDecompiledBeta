package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzcej extends zza {
    public static final Creator<zzcej> CREATOR = new zzcek();
    public String packageName;
    private int versionCode;
    public String zzbpc;
    public zzcjh zzbpd;
    public long zzbpe;
    public boolean zzbpf;
    public String zzbpg;
    public zzcey zzbph;
    public long zzbpi;
    public zzcey zzbpj;
    public long zzbpk;
    public zzcey zzbpl;

    zzcej(int i, String str, String str2, zzcjh com_google_android_gms_internal_zzcjh, long j, boolean z, String str3, zzcey com_google_android_gms_internal_zzcey, long j2, zzcey com_google_android_gms_internal_zzcey2, long j3, zzcey com_google_android_gms_internal_zzcey3) {
        this.versionCode = i;
        this.packageName = str;
        this.zzbpc = str2;
        this.zzbpd = com_google_android_gms_internal_zzcjh;
        this.zzbpe = j;
        this.zzbpf = z;
        this.zzbpg = str3;
        this.zzbph = com_google_android_gms_internal_zzcey;
        this.zzbpi = j2;
        this.zzbpj = com_google_android_gms_internal_zzcey2;
        this.zzbpk = j3;
        this.zzbpl = com_google_android_gms_internal_zzcey3;
    }

    zzcej(zzcej com_google_android_gms_internal_zzcej) {
        this.versionCode = 1;
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        this.packageName = com_google_android_gms_internal_zzcej.packageName;
        this.zzbpc = com_google_android_gms_internal_zzcej.zzbpc;
        this.zzbpd = com_google_android_gms_internal_zzcej.zzbpd;
        this.zzbpe = com_google_android_gms_internal_zzcej.zzbpe;
        this.zzbpf = com_google_android_gms_internal_zzcej.zzbpf;
        this.zzbpg = com_google_android_gms_internal_zzcej.zzbpg;
        this.zzbph = com_google_android_gms_internal_zzcej.zzbph;
        this.zzbpi = com_google_android_gms_internal_zzcej.zzbpi;
        this.zzbpj = com_google_android_gms_internal_zzcej.zzbpj;
        this.zzbpk = com_google_android_gms_internal_zzcej.zzbpk;
        this.zzbpl = com_google_android_gms_internal_zzcej.zzbpl;
    }

    zzcej(String str, String str2, zzcjh com_google_android_gms_internal_zzcjh, long j, boolean z, String str3, zzcey com_google_android_gms_internal_zzcey, long j2, zzcey com_google_android_gms_internal_zzcey2, long j3, zzcey com_google_android_gms_internal_zzcey3) {
        this.versionCode = 1;
        this.packageName = str;
        this.zzbpc = str2;
        this.zzbpd = com_google_android_gms_internal_zzcjh;
        this.zzbpe = j;
        this.zzbpf = z;
        this.zzbpg = str3;
        this.zzbph = com_google_android_gms_internal_zzcey;
        this.zzbpi = j2;
        this.zzbpj = com_google_android_gms_internal_zzcey2;
        this.zzbpk = j3;
        this.zzbpl = com_google_android_gms_internal_zzcey3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, this.packageName, false);
        zzd.zza(parcel, 3, this.zzbpc, false);
        zzd.zza(parcel, 4, this.zzbpd, i, false);
        zzd.zza(parcel, 5, this.zzbpe);
        zzd.zza(parcel, 6, this.zzbpf);
        zzd.zza(parcel, 7, this.zzbpg, false);
        zzd.zza(parcel, 8, this.zzbph, i, false);
        zzd.zza(parcel, 9, this.zzbpi);
        zzd.zza(parcel, 10, this.zzbpj, i, false);
        zzd.zza(parcel, 11, this.zzbpk);
        zzd.zza(parcel, 12, this.zzbpl, i, false);
        zzd.zzI(parcel, zze);
    }
}
