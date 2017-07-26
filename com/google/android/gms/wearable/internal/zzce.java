package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzce extends zza {
    public static final Creator<zzce> CREATOR = new zzcf();
    public final int statusCode;
    public final int zzbSF;

    public zzce(int i, int i2) {
        this.statusCode = i;
        this.zzbSF = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zzc(parcel, 3, this.zzbSF);
        zzd.zzI(parcel, zze);
    }
}
