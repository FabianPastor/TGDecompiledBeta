package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzek extends zza {
    public static final Creator<zzek> CREATOR = new zzel();
    private String label;
    private String packageName;
    private long zzbTc;

    public zzek(String str, String str2, long j) {
        this.packageName = str;
        this.label = str2;
        this.zzbTc = j;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.packageName, false);
        zzd.zza(parcel, 3, this.label, false);
        zzd.zza(parcel, 4, this.zzbTc);
        zzd.zzI(parcel, zze);
    }
}
