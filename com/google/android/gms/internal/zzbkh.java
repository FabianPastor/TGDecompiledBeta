package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkh extends zza {
    public static final Creator<zzbkh> CREATOR = new zzbki();
    public final int versionCode;
    public final String zzbPA;
    public final float zzbPB;
    public final int zzbPC;
    public final boolean zzbPD;
    public final int zzbPE;
    public final int zzbPF;
    public final String zzbPr;
    public final zzbkq[] zzbPw;
    public final zzbkd zzbPx;
    public final zzbkd zzbPy;
    public final zzbkd zzbPz;

    public zzbkh(int i, zzbkq[] com_google_android_gms_internal_zzbkqArr, zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2, zzbkd com_google_android_gms_internal_zzbkd3, String str, float f, String str2, int i2, boolean z, int i3, int i4) {
        this.versionCode = i;
        this.zzbPw = com_google_android_gms_internal_zzbkqArr;
        this.zzbPx = com_google_android_gms_internal_zzbkd;
        this.zzbPy = com_google_android_gms_internal_zzbkd2;
        this.zzbPz = com_google_android_gms_internal_zzbkd3;
        this.zzbPA = str;
        this.zzbPB = f;
        this.zzbPr = str2;
        this.zzbPC = i2;
        this.zzbPD = z;
        this.zzbPE = i3;
        this.zzbPF = i4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbki.zza(this, parcel, i);
    }
}
