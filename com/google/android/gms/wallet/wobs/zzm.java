package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzm extends zza {
    public static final Creator<zzm> CREATOR = new zzn();
    private long zzbQR;
    private long zzbQS;

    zzm() {
    }

    public zzm(long j, long j2) {
        this.zzbQR = j;
        this.zzbQS = j2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQR);
        zzd.zza(parcel, 3, this.zzbQS);
        zzd.zzI(parcel, zze);
    }
}
