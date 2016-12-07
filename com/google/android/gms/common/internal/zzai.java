package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzai implements Creator<zzah> {
    static void zza(zzah com_google_android_gms_common_internal_zzah, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzah.mVersionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_common_internal_zzah.zzxD());
        zzc.zzc(parcel, 3, com_google_android_gms_common_internal_zzah.zzxE());
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzah.zzxF(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaR(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcU(i);
    }

    public zzah zzaR(Parcel parcel) {
        int i = 0;
        int zzaU = zzb.zzaU(parcel);
        Scope[] scopeArr = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 4:
                    scopeArr = (Scope[]) zzb.zzb(parcel, zzaT, Scope.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzah(i3, i2, i, scopeArr);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzah[] zzcU(int i) {
        return new zzah[i];
    }
}
