package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzdks extends zzbfm {
    public static final Creator<zzdks> CREATOR = new zzdkt();
    private byte[] zzleg;

    zzdks() {
        this(new byte[0]);
    }

    public zzdks(byte[] bArr) {
        this.zzleg = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzleg, false);
        zzbfp.zzai(parcel, zze);
    }
}
