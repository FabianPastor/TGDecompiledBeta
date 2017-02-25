package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzf extends zza {
    public static final Creator<zzf> CREATOR = new zzg();
    byte[] zzbRJ;

    zzf() {
        this(new byte[0]);
    }

    public zzf(byte[] bArr) {
        this.zzbRJ = bArr;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }
}
