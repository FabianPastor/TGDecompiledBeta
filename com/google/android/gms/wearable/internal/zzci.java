package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzci extends zza {
    public static final Creator<zzci> CREATOR = new zzcj();
    public final int statusCode;
    public final zzaa zzbSF;

    public zzci(int i, zzaa com_google_android_gms_wearable_internal_zzaa) {
        this.statusCode = i;
        this.zzbSF = com_google_android_gms_wearable_internal_zzaa;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbSF, i, false);
        zzd.zzI(parcel, zze);
    }
}
