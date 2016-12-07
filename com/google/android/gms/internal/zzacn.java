package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzack.zza;
import com.google.android.gms.internal.zzaco.zzb;

public class zzacn implements Creator<zzb> {
    static void zza(zzb com_google_android_gms_internal_zzaco_zzb, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzaco_zzb.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzaco_zzb.zzaA, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzaco_zzb.zzaFO, i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdd(i);
    }

    public zzb zzbb(Parcel parcel) {
        zza com_google_android_gms_internal_zzack_zza = null;
        int zzaU = com.google.android.gms.common.internal.safeparcel.zzb.zzaU(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = com.google.android.gms.common.internal.safeparcel.zzb.zzaT(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzcW(zzaT)) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    str = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaT);
                    break;
                case 3:
                    com_google_android_gms_internal_zzack_zza = (zza) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, zza.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzb(i, str, com_google_android_gms_internal_zzack_zza);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzb[] zzdd(int i) {
        return new zzb[i];
    }
}
