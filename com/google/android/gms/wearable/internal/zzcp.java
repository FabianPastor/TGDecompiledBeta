package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzcp extends zza {
    public static final Creator<zzcp> CREATOR = new zzco();
    private int statusCode;
    private boolean zzbSH;

    public zzcp(int i, boolean z) {
        this.statusCode = i;
        this.zzbSH = z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbSH);
        zzd.zzI(parcel, zze);
    }
}
