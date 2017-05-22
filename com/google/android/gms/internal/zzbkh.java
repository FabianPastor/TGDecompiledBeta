package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkh extends zza {
    public static final Creator<zzbkh> CREATOR = new zzbki();
    public final int versionCode;
    public final zzbkd zzbPA;
    public final zzbkd zzbPB;
    public final zzbkd zzbPC;
    public final String zzbPD;
    public final float zzbPE;
    public final int zzbPF;
    public final boolean zzbPG;
    public final int zzbPH;
    public final int zzbPI;
    public final String zzbPu;
    public final zzbkq[] zzbPz;

    public zzbkh(int i, zzbkq[] com_google_android_gms_internal_zzbkqArr, zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2, zzbkd com_google_android_gms_internal_zzbkd3, String str, float f, String str2, int i2, boolean z, int i3, int i4) {
        this.versionCode = i;
        this.zzbPz = com_google_android_gms_internal_zzbkqArr;
        this.zzbPA = com_google_android_gms_internal_zzbkd;
        this.zzbPB = com_google_android_gms_internal_zzbkd2;
        this.zzbPC = com_google_android_gms_internal_zzbkd3;
        this.zzbPD = str;
        this.zzbPE = f;
        this.zzbPu = str2;
        this.zzbPF = i2;
        this.zzbPG = z;
        this.zzbPH = i3;
        this.zzbPI = i4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbki.zza(this, parcel, i);
    }
}
