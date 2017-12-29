package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzfy extends zzbfm {
    public static final Creator<zzfy> CREATOR = new zzfz();
    public final int statusCode;

    public zzfy(int i) {
        this.statusCode = i;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zzai(parcel, zze);
    }
}
