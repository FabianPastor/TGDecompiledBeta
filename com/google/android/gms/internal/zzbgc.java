package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbgc extends zza {
    public static final Creator<zzbgc> CREATOR = new zzbgd();
    private final zzbge zzaIB;
    private int zzaku;

    zzbgc(int i, zzbge com_google_android_gms_internal_zzbge) {
        this.zzaku = i;
        this.zzaIB = com_google_android_gms_internal_zzbge;
    }

    private zzbgc(zzbge com_google_android_gms_internal_zzbge) {
        this.zzaku = 1;
        this.zzaIB = com_google_android_gms_internal_zzbge;
    }

    public static zzbgc zza(zzbgk<?, ?> com_google_android_gms_internal_zzbgk___) {
        if (com_google_android_gms_internal_zzbgk___ instanceof zzbge) {
            return new zzbgc((zzbge) com_google_android_gms_internal_zzbgk___);
        }
        throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzaIB, i, false);
        zzd.zzI(parcel, zze);
    }

    public final zzbgk<?, ?> zzrK() {
        if (this.zzaIB != null) {
            return this.zzaIB;
        }
        throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
    }
}
