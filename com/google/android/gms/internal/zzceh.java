package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzceh extends zza {
    public static final Creator<zzceh> CREATOR = new zzcei();
    public final String packageName;
    public final String zzbgW;
    public final String zzboQ;
    public final String zzboR;
    public final long zzboS;
    public final long zzboT;
    public final String zzboU;
    public final boolean zzboV;
    public final boolean zzboW;
    public final long zzboX;
    public final String zzboY;
    public final long zzboZ;
    public final long zzbpa;
    public final int zzbpb;

    zzceh(String str, String str2, String str3, long j, String str4, long j2, long j3, String str5, boolean z, boolean z2, String str6, long j4, long j5, int i) {
        zzbo.zzcF(str);
        this.packageName = str;
        if (TextUtils.isEmpty(str2)) {
            str2 = null;
        }
        this.zzboQ = str2;
        this.zzbgW = str3;
        this.zzboX = j;
        this.zzboR = str4;
        this.zzboS = j2;
        this.zzboT = j3;
        this.zzboU = str5;
        this.zzboV = z;
        this.zzboW = z2;
        this.zzboY = str6;
        this.zzboZ = j4;
        this.zzbpa = j5;
        this.zzbpb = i;
    }

    zzceh(String str, String str2, String str3, String str4, long j, long j2, String str5, boolean z, boolean z2, long j3, String str6, long j4, long j5, int i) {
        this.packageName = str;
        this.zzboQ = str2;
        this.zzbgW = str3;
        this.zzboX = j3;
        this.zzboR = str4;
        this.zzboS = j;
        this.zzboT = j2;
        this.zzboU = str5;
        this.zzboV = z;
        this.zzboW = z2;
        this.zzboY = str6;
        this.zzboZ = j4;
        this.zzbpa = j5;
        this.zzbpb = i;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.packageName, false);
        zzd.zza(parcel, 3, this.zzboQ, false);
        zzd.zza(parcel, 4, this.zzbgW, false);
        zzd.zza(parcel, 5, this.zzboR, false);
        zzd.zza(parcel, 6, this.zzboS);
        zzd.zza(parcel, 7, this.zzboT);
        zzd.zza(parcel, 8, this.zzboU, false);
        zzd.zza(parcel, 9, this.zzboV);
        zzd.zza(parcel, 10, this.zzboW);
        zzd.zza(parcel, 11, this.zzboX);
        zzd.zza(parcel, 12, this.zzboY, false);
        zzd.zza(parcel, 13, this.zzboZ);
        zzd.zza(parcel, 14, this.zzbpa);
        zzd.zzc(parcel, 15, this.zzbpb);
        zzd.zzI(parcel, zze);
    }
}
