package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzbgu implements Creator<zzbgt> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        zzbgo com_google_android_gms_internal_zzbgo = null;
        int zzd = zzb.zzd(parcel);
        int i = 0;
        Parcel parcel2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    parcel2 = zzb.zzD(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_internal_zzbgo = (zzbgo) zzb.zza(parcel, readInt, zzbgo.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzbgt(i, parcel2, com_google_android_gms_internal_zzbgo);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzbgt[i];
    }
}
