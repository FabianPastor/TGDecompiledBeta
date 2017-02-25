package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.internal.zzacp.zza;

public class zzacr implements Creator<zza> {
    static void zza(zza com_google_android_gms_internal_zzacp_zza, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacp_zza.versionCode);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzacp_zza.zzaGV, false);
        zzc.zzc(parcel, 3, com_google_android_gms_internal_zzacp_zza.zzaGW);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbd(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdh(i);
    }

    public zza zzbd(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zza(i2, str, i);
        }
        throw new zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zza[] zzdh(int i) {
        return new zza[i];
    }
}
