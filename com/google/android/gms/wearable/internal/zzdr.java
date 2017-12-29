package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzdr extends zzbfm {
    public static final Creator<zzdr> CREATOR = new zzdq();
    private int statusCode;
    private boolean zzlkh;

    public zzdr(int i, boolean z) {
        this.statusCode = i;
        this.zzlkh = z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlkh);
        zzbfp.zzai(parcel, zze);
    }
}
