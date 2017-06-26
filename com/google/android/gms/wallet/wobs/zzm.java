package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzm extends zza {
    public static final Creator<zzm> CREATOR = new zzn();
    private long zzbQP;
    private long zzbQQ;

    zzm() {
    }

    public zzm(long j, long j2) {
        this.zzbQP = j;
        this.zzbQQ = j2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQP);
        zzd.zza(parcel, 3, this.zzbQQ);
        zzd.zzI(parcel, zze);
    }
}
