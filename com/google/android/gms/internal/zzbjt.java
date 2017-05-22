package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbjt extends zza {
    public static final Creator<zzbjt> CREATOR = new zzbju();
    public int mode;
    public final int versionCode;
    public int zzbPh;
    public int zzbPi;
    public boolean zzbPj;
    public boolean zzbPk;
    public float zzbPl;

    public zzbjt() {
        this.versionCode = 2;
    }

    public zzbjt(int i, int i2, int i3, int i4, boolean z, boolean z2, float f) {
        this.versionCode = i;
        this.mode = i2;
        this.zzbPh = i3;
        this.zzbPi = i4;
        this.zzbPj = z;
        this.zzbPk = z2;
        this.zzbPl = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbju.zza(this, parcel, i);
    }
}
