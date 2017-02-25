package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzaco implements Creator<zzacn> {
    static void zza(zzacn com_google_android_gms_internal_zzacn, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, com_google_android_gms_internal_zzacn.zzaiI);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzacn.zzyo(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdf(i);
    }

    public zzacn zzbb(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        zzacp com_google_android_gms_internal_zzacp = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    com_google_android_gms_internal_zzacp = (zzacp) zzb.zza(parcel, zzaX, zzacp.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzacn(i, com_google_android_gms_internal_zzacp);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzacn[] zzdf(int i) {
        return new zzacn[i];
    }
}
