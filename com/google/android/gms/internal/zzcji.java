package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class zzcji extends zza {
    public static final Creator<zzcji> CREATOR = new zzcjj();
    public final String name;
    private int versionCode;
    private String zzaIF;
    public final String zzbpc;
    private Float zzbuA;
    private Double zzbuB;
    public final long zzbuy;
    private Long zzbuz;

    zzcji(int i, String str, long j, Long l, Float f, String str2, String str3, Double d) {
        Double d2 = null;
        this.versionCode = i;
        this.name = str;
        this.zzbuy = j;
        this.zzbuz = l;
        this.zzbuA = null;
        if (i == 1) {
            if (f != null) {
                d2 = Double.valueOf(f.doubleValue());
            }
            this.zzbuB = d2;
        } else {
            this.zzbuB = d;
        }
        this.zzaIF = str2;
        this.zzbpc = str3;
    }

    zzcji(zzcjk com_google_android_gms_internal_zzcjk) {
        this(com_google_android_gms_internal_zzcjk.mName, com_google_android_gms_internal_zzcjk.zzbuC, com_google_android_gms_internal_zzcjk.mValue, com_google_android_gms_internal_zzcjk.mOrigin);
    }

    zzcji(String str, long j, Object obj, String str2) {
        zzbo.zzcF(str);
        this.versionCode = 2;
        this.name = str;
        this.zzbuy = j;
        this.zzbpc = str2;
        if (obj == null) {
            this.zzbuz = null;
            this.zzbuA = null;
            this.zzbuB = null;
            this.zzaIF = null;
        } else if (obj instanceof Long) {
            this.zzbuz = (Long) obj;
            this.zzbuA = null;
            this.zzbuB = null;
            this.zzaIF = null;
        } else if (obj instanceof String) {
            this.zzbuz = null;
            this.zzbuA = null;
            this.zzbuB = null;
            this.zzaIF = (String) obj;
        } else if (obj instanceof Double) {
            this.zzbuz = null;
            this.zzbuA = null;
            this.zzbuB = (Double) obj;
            this.zzaIF = null;
        } else {
            throw new IllegalArgumentException("User attribute given of un-supported type");
        }
    }

    public final Object getValue() {
        return this.zzbuz != null ? this.zzbuz : this.zzbuB != null ? this.zzbuB : this.zzaIF != null ? this.zzaIF : null;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, this.name, false);
        zzd.zza(parcel, 3, this.zzbuy);
        zzd.zza(parcel, 4, this.zzbuz, false);
        zzd.zza(parcel, 5, null, false);
        zzd.zza(parcel, 6, this.zzaIF, false);
        zzd.zza(parcel, 7, this.zzbpc, false);
        zzd.zza(parcel, 8, this.zzbuB, false);
        zzd.zzI(parcel, zze);
    }
}
