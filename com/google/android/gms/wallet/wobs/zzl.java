package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzl extends zza {
    public static final Creator<zzl> CREATOR = new zzm();
    long zzbSJ;
    long zzbSK;

    zzl() {
    }

    public zzl(long j, long j2) {
        this.zzbSJ = j;
        this.zzbSK = j2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzm.zza(this, parcel, i);
    }
}
