package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbht extends zza {
    public static final Creator<zzbht> CREATOR = new zzbhu();
    public final int versionCode;
    public final zzbhg zzbNC;
    public final zzbhg zzbND;
    public final String zzbNF;
    public final float zzbNG;
    public final zzbho[] zzbNM;
    public final boolean zzbNN;
    public final String zzbNw;

    public zzbht(int i, zzbho[] com_google_android_gms_internal_zzbhoArr, zzbhg com_google_android_gms_internal_zzbhg, zzbhg com_google_android_gms_internal_zzbhg2, String str, float f, String str2, boolean z) {
        this.versionCode = i;
        this.zzbNM = com_google_android_gms_internal_zzbhoArr;
        this.zzbNC = com_google_android_gms_internal_zzbhg;
        this.zzbND = com_google_android_gms_internal_zzbhg2;
        this.zzbNF = str;
        this.zzbNG = f;
        this.zzbNw = str2;
        this.zzbNN = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhu.zza(this, parcel, i);
    }
}
