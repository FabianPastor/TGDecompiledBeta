package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.internal.zzack.zzb;

public class zzacf extends zza {
    public static final Creator<zzacf> CREATOR = new zzacg();
    final int mVersionCode;
    private final zzach zzaFu;

    zzacf(int i, zzach com_google_android_gms_internal_zzach) {
        this.mVersionCode = i;
        this.zzaFu = com_google_android_gms_internal_zzach;
    }

    private zzacf(zzach com_google_android_gms_internal_zzach) {
        this.mVersionCode = 1;
        this.zzaFu = com_google_android_gms_internal_zzach;
    }

    public static zzacf zza(zzb<?, ?> com_google_android_gms_internal_zzack_zzb___) {
        if (com_google_android_gms_internal_zzack_zzb___ instanceof zzach) {
            return new zzacf((zzach) com_google_android_gms_internal_zzack_zzb___);
        }
        throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzacg.zza(this, parcel, i);
    }

    zzach zzxH() {
        return this.zzaFu;
    }

    public zzb<?, ?> zzxI() {
        if (this.zzaFu != null) {
            return this.zzaFu;
        }
        throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
    }
}
