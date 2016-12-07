package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaxq implements Creator<zzaxp> {
    static void zza(zzaxp com_google_android_gms_internal_zzaxp, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzaxp.mVersionCode);
        zzc.zzc(parcel, 2, com_google_android_gms_internal_zzaxp.zzOk());
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzaxp.zzOl(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziO(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmI(i);
    }

    public zzaxp zziO(Parcel parcel) {
        int i = 0;
        int zzaU = zzb.zzaU(parcel);
        Intent intent = null;
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
                case 3:
                    intent = (Intent) zzb.zza(parcel, zzaT, Intent.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzaxp(i2, i, intent);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzaxp[] zzmI(int i) {
        return new zzaxp[i];
    }
}
