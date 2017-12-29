package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzfq extends zzbfm {
    public static final Creator<zzfq> CREATOR = new zzfr();
    public final int statusCode;
    public final zzay zzlje;

    public zzfq(int i, zzay com_google_android_gms_wearable_internal_zzay) {
        this.statusCode = i;
        this.zzlje = com_google_android_gms_wearable_internal_zzay;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlje, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
