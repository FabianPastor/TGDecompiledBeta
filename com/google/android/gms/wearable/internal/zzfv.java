package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzfv implements Creator<zzfu> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        zzdd com_google_android_gms_wearable_internal_zzdd = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_wearable_internal_zzdd = (zzdd) zzbfn.zza(parcel, readInt, zzdd.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzfu(i, com_google_android_gms_wearable_internal_zzdd);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzfu[i];
    }
}
