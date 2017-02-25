package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzag implements Creator<zzaf> {
    static void zza(zzaf com_google_android_gms_common_internal_zzaf, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzaf.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_common_internal_zzaf.zzaEW, false);
        zzc.zza(parcel, 3, com_google_android_gms_common_internal_zzaf.zzyh(), i, false);
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzaf.zzyi());
        zzc.zza(parcel, 5, com_google_android_gms_common_internal_zzaf.zzyj());
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaU(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcZ(i);
    }

    public zzaf zzaU(Parcel parcel) {
        ConnectionResult connectionResult = null;
        boolean z = false;
        int zzaY = zzb.zzaY(parcel);
        boolean z2 = false;
        IBinder iBinder = null;
        int i = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    iBinder = zzb.zzr(parcel, zzaX);
                    break;
                case 3:
                    connectionResult = (ConnectionResult) zzb.zza(parcel, zzaX, ConnectionResult.CREATOR);
                    break;
                case 4:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 5:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzaf(i, iBinder, connectionResult, z2, z);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzaf[] zzcZ(int i) {
        return new zzaf[i];
    }
}
