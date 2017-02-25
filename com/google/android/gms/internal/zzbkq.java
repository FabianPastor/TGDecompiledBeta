package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkq extends zza {
    public static final Creator<zzbkq> CREATOR = new zzbkr();
    public final int versionCode;
    public final zzbkd zzbPB;
    public final zzbkd zzbPC;
    public final String zzbPE;
    public final float zzbPF;
    public final zzbkl[] zzbPL;
    public final boolean zzbPM;
    public final String zzbPv;

    public zzbkq(int i, zzbkl[] com_google_android_gms_internal_zzbklArr, zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2, String str, float f, String str2, boolean z) {
        this.versionCode = i;
        this.zzbPL = com_google_android_gms_internal_zzbklArr;
        this.zzbPB = com_google_android_gms_internal_zzbkd;
        this.zzbPC = com_google_android_gms_internal_zzbkd2;
        this.zzbPE = str;
        this.zzbPF = f;
        this.zzbPv = str2;
        this.zzbPM = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkr.zza(this, parcel, i);
    }
}
