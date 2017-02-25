package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzad;

public class zzbau extends zza {
    public static final Creator<zzbau> CREATOR = new zzbav();
    final int zzaiI;
    final zzad zzbEx;

    zzbau(int i, zzad com_google_android_gms_common_internal_zzad) {
        this.zzaiI = i;
        this.zzbEx = com_google_android_gms_common_internal_zzad;
    }

    public zzbau(zzad com_google_android_gms_common_internal_zzad) {
        this(1, com_google_android_gms_common_internal_zzad);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbav.zza(this, parcel, i);
    }

    public zzad zzPS() {
        return this.zzbEx;
    }
}
