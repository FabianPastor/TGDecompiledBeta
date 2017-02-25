package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzj extends zza {
    public static final Creator<zzj> CREATOR = new zzk();
    byte[] zzbRM;

    zzj() {
        this(new byte[0]);
    }

    public zzj(byte[] bArr) {
        this.zzbRM = bArr;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzk.zza(this, parcel, i);
    }
}
