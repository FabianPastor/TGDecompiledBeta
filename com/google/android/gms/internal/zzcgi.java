package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;

public final class zzcgi extends zzbfm {
    public static final Creator<zzcgi> CREATOR = new zzcgj();
    public final String packageName;
    public final String zzifm;
    public final String zzixs;
    public final String zzixt;
    public final long zzixu;
    public final long zzixv;
    public final String zzixw;
    public final boolean zzixx;
    public final boolean zzixy;
    public final long zzixz;
    public final String zziya;
    public final long zziyb;
    public final long zziyc;
    public final int zziyd;
    public final boolean zziye;

    zzcgi(String str, String str2, String str3, long j, String str4, long j2, long j3, String str5, boolean z, boolean z2, String str6, long j4, long j5, int i, boolean z3) {
        zzbq.zzgm(str);
        this.packageName = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        this.zzixs = str2;
        this.zzifm = str3;
        this.zzixz = j;
        this.zzixt = str4;
        this.zzixu = j2;
        this.zzixv = j3;
        this.zzixw = str5;
        this.zzixx = z;
        this.zzixy = z2;
        this.zziya = str6;
        this.zziyb = j4;
        this.zziyc = j5;
        this.zziyd = i;
        this.zziye = z3;
    }

    zzcgi(String str, String str2, String str3, String str4, long j, long j2, String str5, boolean z, boolean z2, long j3, String str6, long j4, long j5, int i, boolean z3) {
        this.packageName = str;
        this.zzixs = str2;
        this.zzifm = str3;
        this.zzixz = j3;
        this.zzixt = str4;
        this.zzixu = j;
        this.zzixv = j2;
        this.zzixw = str5;
        this.zzixx = z;
        this.zzixy = z2;
        this.zziya = str6;
        this.zziyb = j4;
        this.zziyc = j5;
        this.zziyd = i;
        this.zziye = z3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.packageName, false);
        zzbfp.zza(parcel, 3, this.zzixs, false);
        zzbfp.zza(parcel, 4, this.zzifm, false);
        zzbfp.zza(parcel, 5, this.zzixt, false);
        zzbfp.zza(parcel, 6, this.zzixu);
        zzbfp.zza(parcel, 7, this.zzixv);
        zzbfp.zza(parcel, 8, this.zzixw, false);
        zzbfp.zza(parcel, 9, this.zzixx);
        zzbfp.zza(parcel, 10, this.zzixy);
        zzbfp.zza(parcel, 11, this.zzixz);
        zzbfp.zza(parcel, 12, this.zziya, false);
        zzbfp.zza(parcel, 13, this.zziyb);
        zzbfp.zza(parcel, 14, this.zziyc);
        zzbfp.zzc(parcel, 15, this.zziyd);
        zzbfp.zza(parcel, 16, this.zziye);
        zzbfp.zzai(parcel, zze);
    }
}
