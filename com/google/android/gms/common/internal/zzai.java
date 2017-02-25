package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzai implements Creator<zzah> {
    static void zza(zzah com_google_android_gms_common_internal_zzah, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_common_internal_zzah.zzaiI);
        zzc.zzc(parcel, 2, com_google_android_gms_common_internal_zzah.zzyk());
        zzc.zzc(parcel, 3, com_google_android_gms_common_internal_zzah.zzyl());
        zzc.zza(parcel, 4, com_google_android_gms_common_internal_zzah.zzym(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaV(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzda(i);
    }

    public zzah zzaV(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        Scope[] scopeArr = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    scopeArr = (Scope[]) zzb.zzb(parcel, zzaX, Scope.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzah(i3, i2, i, scopeArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzah[] zzda(int i) {
        return new zzah[i];
    }
}
