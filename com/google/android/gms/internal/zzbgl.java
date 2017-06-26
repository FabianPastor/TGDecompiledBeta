package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzbgl implements Creator<zzbgi> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        zzbgb com_google_android_gms_internal_zzbgb = null;
        int i = 0;
        int zzd = zzb.zzd(parcel);
        String str = null;
        String str2 = null;
        boolean z = false;
        int i2 = 0;
        boolean z2 = false;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i4 = zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    i3 = zzb.zzg(parcel, readInt);
                    break;
                case 3:
                    z2 = zzb.zzc(parcel, readInt);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, readInt);
                    break;
                case 5:
                    z = zzb.zzc(parcel, readInt);
                    break;
                case 6:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 7:
                    i = zzb.zzg(parcel, readInt);
                    break;
                case 8:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 9:
                    com_google_android_gms_internal_zzbgb = (zzbgb) zzb.zza(parcel, readInt, zzbgb.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzbgi(i4, i3, z2, i2, z, str2, i, str, com_google_android_gms_internal_zzbgb);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzbgi[i];
    }
}
