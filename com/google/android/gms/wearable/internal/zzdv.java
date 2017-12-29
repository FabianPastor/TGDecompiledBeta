package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzdv extends zzbfm {
    public static final Creator<zzdv> CREATOR = new zzdu();
    private boolean enabled;
    private int statusCode;

    public zzdv(int i, boolean z) {
        this.statusCode = i;
        this.enabled = z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.enabled);
        zzbfp.zzai(parcel, zze);
    }
}
