package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbgw extends zza {
    public static final Creator<zzbgw> CREATOR = new zzbgx();
    public int mode;
    public final int versionCode;
    public int zzbNj;
    public int zzbNk;
    public boolean zzbNl;
    public boolean zzbNm;
    public float zzbNn;

    public zzbgw() {
        this.versionCode = 2;
    }

    public zzbgw(int i, int i2, int i3, int i4, boolean z, boolean z2, float f) {
        this.versionCode = i;
        this.mode = i2;
        this.zzbNj = i3;
        this.zzbNk = i4;
        this.zzbNl = z;
        this.zzbNm = z2;
        this.zzbNn = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbgx.zza(this, parcel, i);
    }
}
