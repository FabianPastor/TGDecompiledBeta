package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjr extends zza {
    public static final Creator<zzbjr> CREATOR = new zzbjs();
    public final float centerX;
    public final float centerY;
    public final float height;
    public final int id;
    public final int versionCode;
    public final float width;
    public final float zzbPc;
    public final float zzbPd;
    public final zzbjx[] zzbPe;
    public final float zzbPf;
    public final float zzbPg;
    public final float zzbPh;

    public zzbjr(int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, zzbjx[] com_google_android_gms_internal_zzbjxArr, float f7, float f8, float f9) {
        this.versionCode = i;
        this.id = i2;
        this.centerX = f;
        this.centerY = f2;
        this.width = f3;
        this.height = f4;
        this.zzbPc = f5;
        this.zzbPd = f6;
        this.zzbPe = com_google_android_gms_internal_zzbjxArr;
        this.zzbPf = f7;
        this.zzbPg = f8;
        this.zzbPh = f9;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbjs.zza(this, parcel, i);
    }
}
