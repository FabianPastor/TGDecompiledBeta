package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzeg extends zzbfm {
    public static final Creator<zzeg> CREATOR = new zzeh();
    public final int statusCode;
    public final zzfo zzlko;

    public zzeg(int i, zzfo com_google_android_gms_wearable_internal_zzfo) {
        this.statusCode = i;
        this.zzlko = com_google_android_gms_wearable_internal_zzfo;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlko, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
