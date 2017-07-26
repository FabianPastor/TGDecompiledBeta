package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fw extends zza {
    public static final Creator<fw> CREATOR = new fx();
    private byte[] zzbPX;

    fw() {
        this(new byte[0]);
    }

    public fw(byte[] bArr) {
        this.zzbPX = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPX, false);
        zzd.zzI(parcel, zze);
    }
}
