package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzaf;

public class zzbax implements Creator<zzbaw> {
    static void zza(zzbaw com_google_android_gms_internal_zzbaw, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbaw.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbaw.zzyh(), i, false);
        zzc.zza(parcel, 3, com_google_android_gms_internal_zzbaw.zzPT(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjy(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzny(i);
    }

    public zzbaw zzjy(Parcel parcel) {
        zzaf com_google_android_gms_common_internal_zzaf = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        ConnectionResult connectionResult = null;
        while (parcel.dataPosition() < zzaY) {
            ConnectionResult connectionResult2;
            int zzg;
            zzaf com_google_android_gms_common_internal_zzaf2;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    zzaf com_google_android_gms_common_internal_zzaf3 = com_google_android_gms_common_internal_zzaf;
                    connectionResult2 = connectionResult;
                    zzg = zzb.zzg(parcel, zzaX);
                    com_google_android_gms_common_internal_zzaf2 = com_google_android_gms_common_internal_zzaf3;
                    break;
                case 2:
                    zzg = i;
                    ConnectionResult connectionResult3 = (ConnectionResult) zzb.zza(parcel, zzaX, ConnectionResult.CREATOR);
                    com_google_android_gms_common_internal_zzaf2 = com_google_android_gms_common_internal_zzaf;
                    connectionResult2 = connectionResult3;
                    break;
                case 3:
                    com_google_android_gms_common_internal_zzaf2 = (zzaf) zzb.zza(parcel, zzaX, zzaf.CREATOR);
                    connectionResult2 = connectionResult;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    com_google_android_gms_common_internal_zzaf2 = com_google_android_gms_common_internal_zzaf;
                    connectionResult2 = connectionResult;
                    zzg = i;
                    break;
            }
            i = zzg;
            connectionResult = connectionResult2;
            com_google_android_gms_common_internal_zzaf = com_google_android_gms_common_internal_zzaf2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbaw(i, connectionResult, com_google_android_gms_common_internal_zzaf);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbaw[] zzny(int i) {
        return new zzbaw[i];
    }
}
