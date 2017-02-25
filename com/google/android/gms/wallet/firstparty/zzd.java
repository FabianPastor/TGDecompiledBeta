package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

@Deprecated
public final class zzd extends zza {
    public static final Creator<zzd> CREATOR = new zze();
    byte[] zzbRH;
    byte[] zzbRI;

    zzd() {
        this(null, null);
    }

    public zzd(byte[] bArr, byte[] bArr2) {
        this.zzbRH = bArr;
        this.zzbRI = bArr2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze.zza(this, parcel, i);
    }
}
