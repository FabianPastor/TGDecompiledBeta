package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzbgn implements Creator<zzbgq> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        zzbgj com_google_android_gms_internal_zzbgj = null;
        int zzd = zzb.zzd(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_internal_zzbgj = (zzbgj) zzb.zza(parcel, readInt, zzbgj.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzbgq(i, str, com_google_android_gms_internal_zzbgj);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzbgq[i];
    }
}
