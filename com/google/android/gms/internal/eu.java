package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class eu extends zza {
    public static final Creator<eu> CREATOR = new ev();
    public int zzbNi;

    public eu(int i) {
        this.zzbNi = i;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbNi);
        zzd.zzI(parcel, zze);
    }
}
