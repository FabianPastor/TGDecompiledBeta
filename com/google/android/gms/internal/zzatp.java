package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzatp implements Creator<zzato> {
    static void zza(zzato com_google_android_gms_internal_zzato, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_internal_zzato.zzLV(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhS(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlB(i);
    }

    public zzato zzhS(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        Bundle bundle = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzato(bundle);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzato[] zzlB(int i) {
        return new zzato[i];
    }
}
