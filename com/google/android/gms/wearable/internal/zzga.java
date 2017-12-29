package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzga extends zzbfm {
    public static final Creator<zzga> CREATOR = new zzgb();
    public final int statusCode;
    public final int zzift;

    public zzga(int i, int i2) {
        this.statusCode = i;
        this.zzift = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zzc(parcel, 3, this.zzift);
        zzbfp.zzai(parcel, zze);
    }
}
