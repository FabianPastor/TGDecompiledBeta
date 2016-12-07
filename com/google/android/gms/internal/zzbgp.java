package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzbgp implements Creator<zzbgo> {
    static void zza(zzbgo com_google_android_gms_internal_zzbgo, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbgo.versionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzbgo.zzbML);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjg(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznv(i);
    }

    public zzbgo zzjg(Parcel parcel) {
        int i = 0;
        int zzaU = zzb.zzaU(parcel);
        int i2 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzbgo(i2, i);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzbgo[] zznv(int i) {
        return new zzbgo[i];
    }
}
