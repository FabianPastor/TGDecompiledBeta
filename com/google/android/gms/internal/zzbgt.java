package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzbgt implements Creator<zzbgs> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        zzbgn com_google_android_gms_internal_zzbgn = null;
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
                    com_google_android_gms_internal_zzbgn = (zzbgn) zzb.zza(parcel, readInt, zzbgn.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzbgs(i, parcel2, com_google_android_gms_internal_zzbgn);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzbgs[i];
    }
}
