package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgp extends zza {
    public static final Creator<zzbgp> CREATOR = new zzbgm();
    final String key;
    private int versionCode;
    final zzbgi<?, ?> zzaIV;

    zzbgp(int i, String str, zzbgi<?, ?> com_google_android_gms_internal_zzbgi___) {
        this.versionCode = i;
        this.key = str;
        this.zzaIV = com_google_android_gms_internal_zzbgi___;
    }

    zzbgp(String str, zzbgi<?, ?> com_google_android_gms_internal_zzbgi___) {
        this.versionCode = 1;
        this.key = str;
        this.zzaIV = com_google_android_gms_internal_zzbgi___;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, this.key, false);
        zzd.zza(parcel, 3, this.zzaIV, i, false);
        zzd.zzI(parcel, zze);
    }
}
