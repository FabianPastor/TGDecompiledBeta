package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgb extends zza {
    public static final Creator<zzbgb> CREATOR = new zzbgc();
    private final zzbgd zzaIB;
    private int zzaku;

    zzbgb(int i, zzbgd com_google_android_gms_internal_zzbgd) {
        this.zzaku = i;
        this.zzaIB = com_google_android_gms_internal_zzbgd;
    }

    private zzbgb(zzbgd com_google_android_gms_internal_zzbgd) {
        this.zzaku = 1;
        this.zzaIB = com_google_android_gms_internal_zzbgd;
    }

    public static zzbgb zza(zzbgj<?, ?> com_google_android_gms_internal_zzbgj___) {
        if (com_google_android_gms_internal_zzbgj___ instanceof zzbgd) {
            return new zzbgb((zzbgd) com_google_android_gms_internal_zzbgj___);
        }
        throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzaIB, i, false);
        zzd.zzI(parcel, zze);
    }

    public final zzbgj<?, ?> zzrK() {
        if (this.zzaIB != null) {
            return this.zzaIB;
        }
        throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
    }
}
