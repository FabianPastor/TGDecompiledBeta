package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzdk extends zzbfm {
    public static final Creator<zzdk> CREATOR = new zzdl();
    public final int statusCode;
    public final zzah zzlkf;

    public zzdk(int i, zzah com_google_android_gms_wearable_internal_zzah) {
        this.statusCode = i;
        this.zzlkf = com_google_android_gms_wearable_internal_zzah;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlkf, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
