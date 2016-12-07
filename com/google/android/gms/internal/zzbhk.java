package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhk extends zza {
    public static final Creator<zzbhk> CREATOR = new zzbhl();
    public final int versionCode;
    public final zzbht[] zzbNB;
    public final zzbhg zzbNC;
    public final zzbhg zzbND;
    public final zzbhg zzbNE;
    public final String zzbNF;
    public final float zzbNG;
    public final int zzbNH;
    public final boolean zzbNI;
    public final int zzbNJ;
    public final int zzbNK;
    public final String zzbNw;

    public zzbhk(int i, zzbht[] com_google_android_gms_internal_zzbhtArr, zzbhg com_google_android_gms_internal_zzbhg, zzbhg com_google_android_gms_internal_zzbhg2, zzbhg com_google_android_gms_internal_zzbhg3, String str, float f, String str2, int i2, boolean z, int i3, int i4) {
        this.versionCode = i;
        this.zzbNB = com_google_android_gms_internal_zzbhtArr;
        this.zzbNC = com_google_android_gms_internal_zzbhg;
        this.zzbND = com_google_android_gms_internal_zzbhg2;
        this.zzbNE = com_google_android_gms_internal_zzbhg3;
        this.zzbNF = str;
        this.zzbNG = f;
        this.zzbNw = str2;
        this.zzbNH = i2;
        this.zzbNI = z;
        this.zzbNJ = i3;
        this.zzbNK = i4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhl.zza(this, parcel, i);
    }
}
