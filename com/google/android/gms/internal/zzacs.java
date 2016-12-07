package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzacs implements Creator<zzacr> {
    static void zza(zzacr com_google_android_gms_internal_zzacr, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacr.getVersionCode());
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzacr.zzya(), false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzacr.zzyb(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbe(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdg(i);
    }

    public zzacr zzbe(Parcel parcel) {
        zzaco com_google_android_gms_internal_zzaco = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        Parcel parcel2 = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    parcel2 = zzb.zzF(parcel, zzaT);
                    break;
                case 3:
                    com_google_android_gms_internal_zzaco = (zzaco) zzb.zza(parcel, zzaT, zzaco.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzacr(i, parcel2, com_google_android_gms_internal_zzaco);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzacr[] zzdg(int i) {
        return new zzacr[i];
    }
}
