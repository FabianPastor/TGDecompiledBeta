package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbr;

public final class zzcxp implements Creator<zzcxo> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        zzbr com_google_android_gms_common_internal_zzbr = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    com_google_android_gms_common_internal_zzbr = (zzbr) zzbfn.zza(parcel, readInt, zzbr.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzcxo(i, com_google_android_gms_common_internal_zzbr);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcxo[i];
    }
}
