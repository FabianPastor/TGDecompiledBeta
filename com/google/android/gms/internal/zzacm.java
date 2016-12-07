package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzack.zza;

public class zzacm implements Creator<zza> {
    static void zza(zza com_google_android_gms_internal_zzack_zza, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzack_zza.getVersionCode());
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzack_zza.zzxL());
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzack_zza.zzxM());
        zzc.zzc(parcel, 4, com_google_android_gms_internal_zzack_zza.zzxN());
        zzc.zza(parcel, 5, com_google_android_gms_internal_zzack_zza.zzxO());
        zzc.zza(parcel, 6, com_google_android_gms_internal_zzack_zza.zzxP(), false);
        zzc.zzc(parcel, 7, com_google_android_gms_internal_zzack_zza.zzxQ());
        zzc.zza(parcel, 8, com_google_android_gms_internal_zzack_zza.zzxS(), false);
        zzc.zza(parcel, 9, com_google_android_gms_internal_zzack_zza.zzxU(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzba(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdc(i);
    }

    public zza zzba(Parcel parcel) {
        zzacf com_google_android_gms_internal_zzacf = null;
        int i = 0;
        int zzaU = zzb.zzaU(parcel);
        String str = null;
        String str2 = null;
        boolean z = false;
        int i2 = 0;
        boolean z2 = false;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i4 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 5:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 6:
                    str2 = zzb.zzq(parcel, zzaT);
                    break;
                case 7:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 8:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                case 9:
                    com_google_android_gms_internal_zzacf = (zzacf) zzb.zza(parcel, zzaT, zzacf.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zza(i4, i3, z2, i2, z, str2, i, str, com_google_android_gms_internal_zzacf);
        }
        throw new zzb.zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zza[] zzdc(int i) {
        return new zza[i];
    }
}
