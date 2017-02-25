package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzad;

public class zzbav implements Creator<zzbau> {
    static void zza(zzbau com_google_android_gms_internal_zzbau, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzbau.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzbau.zzPS(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznx(i);
    }

    public zzbau zzjx(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        zzad com_google_android_gms_common_internal_zzad = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    com_google_android_gms_common_internal_zzad = (zzad) zzb.zza(parcel, zzaX, zzad.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzbau(i, com_google_android_gms_common_internal_zzad);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzbau[] zznx(int i) {
        return new zzbau[i];
    }
}
