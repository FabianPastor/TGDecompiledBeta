package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzbgm implements Creator<zzbgp> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        zzbgi com_google_android_gms_internal_zzbgi = null;
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
                    com_google_android_gms_internal_zzbgi = (zzbgi) zzb.zza(parcel, readInt, zzbgi.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzbgp(i, str, com_google_android_gms_internal_zzbgi);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzbgp[i];
    }
}
