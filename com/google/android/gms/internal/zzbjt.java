package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjt extends zza {
    public static final Creator<zzbjt> CREATOR = new zzbju();
    public int mode;
    public final int versionCode;
    public int zzbPi;
    public int zzbPj;
    public boolean zzbPk;
    public boolean zzbPl;
    public float zzbPm;

    public zzbjt() {
        this.versionCode = 2;
    }

    public zzbjt(int i, int i2, int i3, int i4, boolean z, boolean z2, float f) {
        this.versionCode = i;
        this.mode = i2;
        this.zzbPi = i3;
        this.zzbPj = i4;
        this.zzbPk = z;
        this.zzbPl = z2;
        this.zzbPm = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbju.zza(this, parcel, i);
    }
}
