package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgq extends zza {
    public static final Creator<zzbgq> CREATOR = new zzbgn();
    final String key;
    private int versionCode;
    final zzbgj<?, ?> zzaIV;

    zzbgq(int i, String str, zzbgj<?, ?> com_google_android_gms_internal_zzbgj___) {
        this.versionCode = i;
        this.key = str;
        this.zzaIV = com_google_android_gms_internal_zzbgj___;
    }

    zzbgq(String str, zzbgj<?, ?> com_google_android_gms_internal_zzbgj___) {
        this.versionCode = 1;
        this.key = str;
        this.zzaIV = com_google_android_gms_internal_zzbgj___;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, this.key, false);
        zzd.zza(parcel, 3, this.zzaIV, i, false);
        zzd.zzI(parcel, zze);
    }
}
