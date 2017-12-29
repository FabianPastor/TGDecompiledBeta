package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;

public final class zzcgl extends zzbfm {
    public static final Creator<zzcgl> CREATOR = new zzcgm();
    public String packageName;
    private int versionCode;
    public String zziyf;
    public zzcln zziyg;
    public long zziyh;
    public boolean zziyi;
    public String zziyj;
    public zzcha zziyk;
    public long zziyl;
    public zzcha zziym;
    public long zziyn;
    public zzcha zziyo;

    zzcgl(int i, String str, String str2, zzcln com_google_android_gms_internal_zzcln, long j, boolean z, String str3, zzcha com_google_android_gms_internal_zzcha, long j2, zzcha com_google_android_gms_internal_zzcha2, long j3, zzcha com_google_android_gms_internal_zzcha3) {
        this.versionCode = i;
        this.packageName = str;
        this.zziyf = str2;
        this.zziyg = com_google_android_gms_internal_zzcln;
        this.zziyh = j;
        this.zziyi = z;
        this.zziyj = str3;
        this.zziyk = com_google_android_gms_internal_zzcha;
        this.zziyl = j2;
        this.zziym = com_google_android_gms_internal_zzcha2;
        this.zziyn = j3;
        this.zziyo = com_google_android_gms_internal_zzcha3;
    }

    zzcgl(zzcgl com_google_android_gms_internal_zzcgl) {
        this.versionCode = 1;
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl);
        this.packageName = com_google_android_gms_internal_zzcgl.packageName;
        this.zziyf = com_google_android_gms_internal_zzcgl.zziyf;
        this.zziyg = com_google_android_gms_internal_zzcgl.zziyg;
        this.zziyh = com_google_android_gms_internal_zzcgl.zziyh;
        this.zziyi = com_google_android_gms_internal_zzcgl.zziyi;
        this.zziyj = com_google_android_gms_internal_zzcgl.zziyj;
        this.zziyk = com_google_android_gms_internal_zzcgl.zziyk;
        this.zziyl = com_google_android_gms_internal_zzcgl.zziyl;
        this.zziym = com_google_android_gms_internal_zzcgl.zziym;
        this.zziyn = com_google_android_gms_internal_zzcgl.zziyn;
        this.zziyo = com_google_android_gms_internal_zzcgl.zziyo;
    }

    zzcgl(String str, String str2, zzcln com_google_android_gms_internal_zzcln, long j, boolean z, String str3, zzcha com_google_android_gms_internal_zzcha, long j2, zzcha com_google_android_gms_internal_zzcha2, long j3, zzcha com_google_android_gms_internal_zzcha3) {
        this.versionCode = 1;
        this.packageName = str;
        this.zziyf = str2;
        this.zziyg = com_google_android_gms_internal_zzcln;
        this.zziyh = j;
        this.zziyi = z;
        this.zziyj = str3;
        this.zziyk = com_google_android_gms_internal_zzcha;
        this.zziyl = j2;
        this.zziym = com_google_android_gms_internal_zzcha2;
        this.zziyn = j3;
        this.zziyo = com_google_android_gms_internal_zzcha3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.versionCode);
        zzbfp.zza(parcel, 2, this.packageName, false);
        zzbfp.zza(parcel, 3, this.zziyf, false);
        zzbfp.zza(parcel, 4, this.zziyg, i, false);
        zzbfp.zza(parcel, 5, this.zziyh);
        zzbfp.zza(parcel, 6, this.zziyi);
        zzbfp.zza(parcel, 7, this.zziyj, false);
        zzbfp.zza(parcel, 8, this.zziyk, i, false);
        zzbfp.zza(parcel, 9, this.zziyl);
        zzbfp.zza(parcel, 10, this.zziym, i, false);
        zzbfp.zza(parcel, 11, this.zziyn);
        zzbfp.zza(parcel, 12, this.zziyo, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
