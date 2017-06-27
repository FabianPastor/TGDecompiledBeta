package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzab extends zza {
    public static final Creator<zzab> CREATOR = new zzac();
    private String zzbPU;

    private zzab() {
    }

    zzab(String str) {
        this.zzbPU = str;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPU, false);
        zzd.zzI(parcel, zze);
    }
}