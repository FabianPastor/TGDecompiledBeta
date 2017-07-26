package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fy extends zza {
    public static final Creator<fy> CREATOR = new fz();
    private byte[] zzbPY;

    fy() {
        this(new byte[0]);
    }

    public fy(byte[] bArr) {
        this.zzbPY = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPY, false);
        zzd.zzI(parcel, zze);
    }
}
