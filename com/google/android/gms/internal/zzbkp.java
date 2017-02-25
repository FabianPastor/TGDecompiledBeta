package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkp implements Creator<zzbko> {
    static void zza(zzbko com_google_android_gms_internal_zzbko, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbko.versionCode);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjV(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzor(i);
    }

    public zzbko zzjV(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbko(i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbko[] zzor(int i) {
        return new zzbko[i];
    }
}
