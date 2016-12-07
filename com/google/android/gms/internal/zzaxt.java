package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.List;

public class zzaxt implements Creator<zzaxs> {
    static void zza(zzaxs com_google_android_gms_internal_zzaxs, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzaxs.mVersionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzaxs.zzbCn);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzaxs.zzbCo, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziP(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmJ(i);
    }

    public zzaxs zziP(Parcel parcel) {
        boolean z = false;
        int zzaU = zzb.zzaU(parcel);
        List list = null;
        int i = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 3:
                    list = zzb.zzc(parcel, zzaT, Scope.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzaxs(i, z, list);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzaxs[] zzmJ(int i) {
        return new zzaxs[i];
    }
}
