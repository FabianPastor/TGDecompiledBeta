package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzad;

public class zzaya implements Creator<zzaxz> {
    static void zza(zzaxz com_google_android_gms_internal_zzaxz, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzaxz.mVersionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzaxz.zzOo(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziR(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmM(i);
    }

    public zzaxz zziR(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        zzad com_google_android_gms_common_internal_zzad = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    com_google_android_gms_common_internal_zzad = (zzad) zzb.zza(parcel, zzaT, zzad.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzaxz(i, com_google_android_gms_common_internal_zzad);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzaxz[] zzmM(int i) {
        return new zzaxz[i];
    }
}
