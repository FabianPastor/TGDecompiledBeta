package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzdg extends zzbfm {
    public static final Creator<zzdg> CREATOR = new zzdh();
    public final int statusCode;
    public final int zzlkd;

    public zzdg(int i, int i2) {
        this.statusCode = i;
        this.zzlkd = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zzc(parcel, 3, this.zzlkd);
        zzbfp.zzai(parcel, zze);
    }
}
