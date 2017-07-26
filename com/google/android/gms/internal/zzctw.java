package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.zzbp;

public final class zzctw implements Creator<zzctv> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        int i = 0;
        zzbp com_google_android_gms_common_internal_zzbp = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    com_google_android_gms_common_internal_zzbp = (zzbp) zzb.zza(parcel, readInt, zzbp.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzctv(i, com_google_android_gms_common_internal_zzbp);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzctv[i];
    }
}
