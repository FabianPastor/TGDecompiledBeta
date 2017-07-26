package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbp;

public final class zzctv extends zza {
    public static final Creator<zzctv> CREATOR = new zzctw();
    private int zzaku;
    private zzbp zzbCU;

    zzctv(int i, zzbp com_google_android_gms_common_internal_zzbp) {
        this.zzaku = i;
        this.zzbCU = com_google_android_gms_common_internal_zzbp;
    }

    public zzctv(zzbp com_google_android_gms_common_internal_zzbp) {
        this(1, com_google_android_gms_common_internal_zzbp);
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzbCU, i, false);
        zzd.zzI(parcel, zze);
    }
}
