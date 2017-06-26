package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fv extends zza {
    public static final Creator<fv> CREATOR = new fw();
    private byte[] zzbPV;

    fv() {
        this(new byte[0]);
    }

    public fv(byte[] bArr) {
        this.zzbPV = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPV, false);
        zzd.zzI(parcel, zze);
    }
}
