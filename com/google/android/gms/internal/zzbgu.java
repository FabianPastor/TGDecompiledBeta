package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbgu extends zza {
    public static final Creator<zzbgu> CREATOR = new zzbgv();
    public final float centerX;
    public final float centerY;
    public final float height;
    public final int id;
    public final int versionCode;
    public final float width;
    public final float zzbNd;
    public final float zzbNe;
    public final zzbha[] zzbNf;
    public final float zzbNg;
    public final float zzbNh;
    public final float zzbNi;

    public zzbgu(int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, zzbha[] com_google_android_gms_internal_zzbhaArr, float f7, float f8, float f9) {
        this.versionCode = i;
        this.id = i2;
        this.centerX = f;
        this.centerY = f2;
        this.width = f3;
        this.height = f4;
        this.zzbNd = f5;
        this.zzbNe = f6;
        this.zzbNf = com_google_android_gms_internal_zzbhaArr;
        this.zzbNg = f7;
        this.zzbNh = f8;
        this.zzbNi = f9;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbgv.zza(this, parcel, i);
    }
}
