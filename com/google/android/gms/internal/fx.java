package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fx extends zza {
    public static final Creator<fx> CREATOR = new fy();
    private byte[] zzbPW;

    fx() {
        this(new byte[0]);
    }

    public fx(byte[] bArr) {
        this.zzbPW = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPW, false);
        zzd.zzI(parcel, zze);
    }
}
