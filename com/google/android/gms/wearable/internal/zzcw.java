package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.ConnectionConfiguration;

public final class zzcw extends zza {
    public static final Creator<zzcw> CREATOR = new zzcx();
    private int statusCode;
    private ConnectionConfiguration[] zzbSN;

    public zzcw(int i, ConnectionConfiguration[] connectionConfigurationArr) {
        this.statusCode = i;
        this.zzbSN = connectionConfigurationArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbSN, i, false);
        zzd.zzI(parcel, zze);
    }
}
