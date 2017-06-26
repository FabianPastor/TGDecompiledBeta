package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzen implements Creator<zzem> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        int i = 0;
        zzcb com_google_android_gms_wearable_internal_zzcb = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_wearable_internal_zzcb = (zzcb) zzb.zza(parcel, readInt, zzcb.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzem(i, com_google_android_gms_wearable_internal_zzcb);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzem[i];
    }
}
