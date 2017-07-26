package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzde extends zza {
    public static final Creator<zzde> CREATOR = new zzdf();
    public final int statusCode;
    public final zzeg zzbSQ;

    public zzde(int i, zzeg com_google_android_gms_wearable_internal_zzeg) {
        this.statusCode = i;
        this.zzbSQ = com_google_android_gms_wearable_internal_zzeg;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbSQ, i, false);
        zzd.zzI(parcel, zze);
    }
}
