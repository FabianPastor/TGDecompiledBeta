package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzem extends zza {
    public static final Creator<zzem> CREATOR = new zzen();
    public final int statusCode;
    public final zzcb zzbSP;

    public zzem(int i, zzcb com_google_android_gms_wearable_internal_zzcb) {
        this.statusCode = i;
        this.zzbSP = com_google_android_gms_wearable_internal_zzcb;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbSP, i, false);
        zzd.zzI(parcel, zze);
    }
}
