package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzcr extends zza {
    public static final Creator<zzcr> CREATOR = new zzcq();
    private int statusCode;
    private boolean zzbSK;
    private boolean zzbSL;

    public zzcr(int i, boolean z, boolean z2) {
        this.statusCode = i;
        this.zzbSK = z;
        this.zzbSL = z2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbSK);
        zzd.zza(parcel, 4, this.zzbSL);
        zzd.zzI(parcel, zze);
    }
}
