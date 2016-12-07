package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzad;

public class zzaxz extends zza {
    public static final Creator<zzaxz> CREATOR = new zzaya();
    final int mVersionCode;
    final zzad zzbCr;

    zzaxz(int i, zzad com_google_android_gms_common_internal_zzad) {
        this.mVersionCode = i;
        this.zzbCr = com_google_android_gms_common_internal_zzad;
    }

    public zzaxz(zzad com_google_android_gms_common_internal_zzad) {
        this(1, com_google_android_gms_common_internal_zzad);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaya.zza(this, parcel, i);
    }

    public zzad zzOo() {
        return this.zzbCr;
    }
}
