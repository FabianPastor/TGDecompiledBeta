package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkq extends zza {
    public static final Creator<zzbkq> CREATOR = new zzbkr();
    public final int versionCode;
    public final zzbkd zzbPA;
    public final zzbkd zzbPB;
    public final String zzbPD;
    public final float zzbPE;
    public final zzbkl[] zzbPK;
    public final boolean zzbPL;
    public final String zzbPu;

    public zzbkq(int i, zzbkl[] com_google_android_gms_internal_zzbklArr, zzbkd com_google_android_gms_internal_zzbkd, zzbkd com_google_android_gms_internal_zzbkd2, String str, float f, String str2, boolean z) {
        this.versionCode = i;
        this.zzbPK = com_google_android_gms_internal_zzbklArr;
        this.zzbPA = com_google_android_gms_internal_zzbkd;
        this.zzbPB = com_google_android_gms_internal_zzbkd2;
        this.zzbPD = str;
        this.zzbPE = f;
        this.zzbPu = str2;
        this.zzbPL = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkr.zza(this, parcel, i);
    }
}
