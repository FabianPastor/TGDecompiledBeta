package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class et extends zza {
    public static final Creator<et> CREATOR = new eu();
    public int zzbNg;

    public et(int i) {
        this.zzbNg = i;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbNg);
        zzd.zzI(parcel, zze);
    }
}
