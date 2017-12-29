package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzchb implements Creator<zzcha> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzbfn.zzd(parcel);
        long j = 0;
        zzcgx com_google_android_gms_internal_zzcgx = null;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_internal_zzcgx = (zzcgx) zzbfn.zza(parcel, readInt, zzcgx.CREATOR);
                    break;
                case 4:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 5:
                    j = zzbfn.zzi(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzcha(str2, com_google_android_gms_internal_zzcgx, str, j);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcha[i];
    }
}
