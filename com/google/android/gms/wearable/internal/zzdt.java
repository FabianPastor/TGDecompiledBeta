package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzdt extends zzbfm {
    public static final Creator<zzdt> CREATOR = new zzds();
    private int statusCode;
    private boolean zzlki;
    private boolean zzlkj;

    public zzdt(int i, boolean z, boolean z2) {
        this.statusCode = i;
        this.zzlki = z;
        this.zzlkj = z2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlki);
        zzbfp.zza(parcel, 4, this.zzlkj);
        zzbfp.zzai(parcel, zze);
    }
}
