package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzdkq extends zzbfm {
    public static final Creator<zzdkq> CREATOR = new zzdkr();
    private byte[] zzlef;

    zzdkq() {
        this(new byte[0]);
    }

    public zzdkq(byte[] bArr) {
        this.zzlef = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlef, false);
        zzbfp.zzai(parcel, zze);
    }
}
