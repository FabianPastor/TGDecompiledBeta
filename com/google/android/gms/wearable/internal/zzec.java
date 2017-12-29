package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzec extends zzbfm {
    public static final Creator<zzec> CREATOR = new zzed();
    public final int statusCode;
    public final zzdd zzlkn;

    public zzec(int i, zzdd com_google_android_gms_wearable_internal_zzdd) {
        this.statusCode = i;
        this.zzlkn = com_google_android_gms_wearable_internal_zzdd;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlkn, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
