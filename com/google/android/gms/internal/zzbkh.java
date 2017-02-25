package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkh extends zza {
    public static final Creator<zzbkh> CREATOR = new zzbki();
    public final int versionCode;
    public final zzbkq[] zzbPA;
    public final zzbkd zzbPB;
    public final zzbkd zzbPC;
    public final zzbkd zzbPD;
    public final String zzbPE;
    public final float zzbPF;
    public final int zzbPG;
    public final boolean zzbPH;
    public final int zzbPI;
    public final int zzbPJ;
    public final String zzbPv;

    public zzbkh(int i, zzbkq[] com_google_android_gms_internal_zzbkqArr, zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2, zzbkd com_google_android_gms_internal_zzbkd3, String str, float f, String str2, int i2, boolean z, int i3, int i4) {
        this.versionCode = i;
        this.zzbPA = com_google_android_gms_internal_zzbkqArr;
        this.zzbPB = com_google_android_gms_internal_zzbkd;
        this.zzbPC = com_google_android_gms_internal_zzbkd2;
        this.zzbPD = com_google_android_gms_internal_zzbkd3;
        this.zzbPE = str;
        this.zzbPF = f;
        this.zzbPv = str2;
        this.zzbPG = i2;
        this.zzbPH = z;
        this.zzbPI = i3;
        this.zzbPJ = i4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbki.zza(this, parcel, i);
    }
}
