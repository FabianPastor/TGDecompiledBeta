package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzaf;

public class zzayc implements Creator<zzayb> {
    static void zza(zzayb com_google_android_gms_internal_zzayb, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzayb.mVersionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzayb.zzxA(), i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzayb.zzOp(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zziS(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzmN(i);
    }

    public zzayb zziS(Parcel parcel) {
        zzaf com_google_android_gms_common_internal_zzaf = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        ConnectionResult connectionResult = null;
        while (parcel.dataPosition() < zzaU) {
            ConnectionResult connectionResult2;
            int zzg;
            zzaf com_google_android_gms_common_internal_zzaf2;
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    zzaf com_google_android_gms_common_internal_zzaf3 = com_google_android_gms_common_internal_zzaf;
                    connectionResult2 = connectionResult;
                    zzg = zzb.zzg(parcel, zzaT);
                    com_google_android_gms_common_internal_zzaf2 = com_google_android_gms_common_internal_zzaf3;
                    break;
                case 2:
                    zzg = i;
                    ConnectionResult connectionResult3 = (ConnectionResult) zzb.zza(parcel, zzaT, ConnectionResult.CREATOR);
                    com_google_android_gms_common_internal_zzaf2 = com_google_android_gms_common_internal_zzaf;
                    connectionResult2 = connectionResult3;
                    break;
                case 3:
                    com_google_android_gms_common_internal_zzaf2 = (zzaf) zzb.zza(parcel, zzaT, zzaf.CREATOR);
                    connectionResult2 = connectionResult;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    com_google_android_gms_common_internal_zzaf2 = com_google_android_gms_common_internal_zzaf;
                    connectionResult2 = connectionResult;
                    zzg = i;
                    break;
            }
            i = zzg;
            connectionResult = connectionResult2;
            com_google_android_gms_common_internal_zzaf = com_google_android_gms_common_internal_zzaf2;
        }
        if (parcel.dataPosition() == zzaU) {
            return new zzayb(i, connectionResult, com_google_android_gms_common_internal_zzaf);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public zzayb[] zzmN(int i) {
        return new zzayb[i];
    }
}
