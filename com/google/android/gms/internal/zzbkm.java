package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbkm implements Creator<zzbkl> {
    static void zza(zzbkl com_google_android_gms_internal_zzbkl, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbkl.versionCode);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjU(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoq(i);
    }

    public zzbkl zzjU(Parcel parcel) {
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
            return new zzbkl(i);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbkl[] zzoq(int i) {
        return new zzbkl[i];
    }
}
