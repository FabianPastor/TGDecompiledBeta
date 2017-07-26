package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzcek extends zza {
    public static final Creator<zzcek> CREATOR = new zzcel();
    public String packageName;
    private int versionCode;
    public String zzbpc;
    public zzcji zzbpd;
    public long zzbpe;
    public boolean zzbpf;
    public String zzbpg;
    public zzcez zzbph;
    public long zzbpi;
    public zzcez zzbpj;
    public long zzbpk;
    public zzcez zzbpl;

    zzcek(int i, String str, String str2, zzcji com_google_android_gms_internal_zzcji, long j, boolean z, String str3, zzcez com_google_android_gms_internal_zzcez, long j2, zzcez com_google_android_gms_internal_zzcez2, long j3, zzcez com_google_android_gms_internal_zzcez3) {
        this.versionCode = i;
        this.packageName = str;
        this.zzbpc = str2;
        this.zzbpd = com_google_android_gms_internal_zzcji;
        this.zzbpe = j;
        this.zzbpf = z;
        this.zzbpg = str3;
        this.zzbph = com_google_android_gms_internal_zzcez;
        this.zzbpi = j2;
        this.zzbpj = com_google_android_gms_internal_zzcez2;
        this.zzbpk = j3;
        this.zzbpl = com_google_android_gms_internal_zzcez3;
    }

    zzcek(zzcek com_google_android_gms_internal_zzcek) {
        this.versionCode = 1;
        zzbo.zzu(com_google_android_gms_internal_zzcek);
        this.packageName = com_google_android_gms_internal_zzcek.packageName;
        this.zzbpc = com_google_android_gms_internal_zzcek.zzbpc;
        this.zzbpd = com_google_android_gms_internal_zzcek.zzbpd;
        this.zzbpe = com_google_android_gms_internal_zzcek.zzbpe;
        this.zzbpf = com_google_android_gms_internal_zzcek.zzbpf;
        this.zzbpg = com_google_android_gms_internal_zzcek.zzbpg;
        this.zzbph = com_google_android_gms_internal_zzcek.zzbph;
        this.zzbpi = com_google_android_gms_internal_zzcek.zzbpi;
        this.zzbpj = com_google_android_gms_internal_zzcek.zzbpj;
        this.zzbpk = com_google_android_gms_internal_zzcek.zzbpk;
        this.zzbpl = com_google_android_gms_internal_zzcek.zzbpl;
    }

    zzcek(String str, String str2, zzcji com_google_android_gms_internal_zzcji, long j, boolean z, String str3, zzcez com_google_android_gms_internal_zzcez, long j2, zzcez com_google_android_gms_internal_zzcez2, long j3, zzcez com_google_android_gms_internal_zzcez3) {
        this.versionCode = 1;
        this.packageName = str;
        this.zzbpc = str2;
        this.zzbpd = com_google_android_gms_internal_zzcji;
        this.zzbpe = j;
        this.zzbpf = z;
        this.zzbpg = str3;
        this.zzbph = com_google_android_gms_internal_zzcez;
        this.zzbpi = j2;
        this.zzbpj = com_google_android_gms_internal_zzcez2;
        this.zzbpk = j3;
        this.zzbpl = com_google_android_gms_internal_zzcez3;
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
