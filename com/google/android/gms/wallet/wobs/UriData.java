package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class UriData extends zzbfm {
    public static final Creator<UriData> CREATOR = new zzl();
    private String description;
    private String zzdzv;

    UriData() {
    }

    public UriData(String str, String str2) {
        this.zzdzv = str;
        this.description = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzdzv, false);
        zzbfp.zza(parcel, 3, this.description, false);
        zzbfp.zzai(parcel, zze);
    }
}
