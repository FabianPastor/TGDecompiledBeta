package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbgo extends zza {
    public static final Creator<zzbgo> CREATOR = new zzbgp();
    final int versionCode;
    public int zzbML;

    public zzbgo() {
        this.versionCode = 1;
    }

    public zzbgo(int i, int i2) {
        this.versionCode = i;
        this.zzbML = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbgp.zza(this, parcel, i);
    }
}
