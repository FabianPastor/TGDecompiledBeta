package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzar extends zzbfm {
    public static final Creator<zzar> CREATOR = new zzas();
    private String zzlee;

    private zzar() {
    }

    zzar(String str) {
        this.zzlee = str;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlee, false);
        zzbfp.zzai(parcel, zze);
    }
}
