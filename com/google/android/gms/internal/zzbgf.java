package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgf extends zza {
    public static final Creator<zzbgf> CREATOR = new zzbgh();
    private int versionCode;
    final String zzaIF;
    final int zzaIG;

    zzbgf(int i, String str, int i2) {
        this.versionCode = i;
        this.zzaIF = str;
        this.zzaIG = i2;
    }

    zzbgf(String str, int i) {
        this.versionCode = 1;
        this.zzaIF = str;
        this.zzaIG = i;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, this.zzaIF, false);
        zzd.zzc(parcel, 3, this.zzaIG);
        zzd.zzI(parcel, zze);
    }
}
