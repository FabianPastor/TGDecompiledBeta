package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkq extends zza {
    public static final Creator<zzbkq> CREATOR = new zzbkr();
    public final int versionCode;
    public final String zzbPA;
    public final float zzbPB;
    public final zzbkl[] zzbPH;
    public final boolean zzbPI;
    public final String zzbPr;
    public final zzbkd zzbPx;
    public final zzbkd zzbPy;

    public zzbkq(int i, zzbkl[] com_google_android_gms_internal_zzbklArr, zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2, String str, float f, String str2, boolean z) {
        this.versionCode = i;
        this.zzbPH = com_google_android_gms_internal_zzbklArr;
        this.zzbPx = com_google_android_gms_internal_zzbkd;
        this.zzbPy = com_google_android_gms_internal_zzbkd2;
        this.zzbPA = str;
        this.zzbPB = f;
        this.zzbPr = str2;
        this.zzbPI = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkr.zza(this, parcel, i);
    }
}
