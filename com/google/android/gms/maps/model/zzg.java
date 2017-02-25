package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzg implements Creator<MapStyleOptions> {
    static void zza(MapStyleOptions mapStyleOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, mapStyleOptions.zzJK(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhD(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzll(i);
    }

    public MapStyleOptions zzhD(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new MapStyleOptions(str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public MapStyleOptions[] zzll(int i) {
        return new MapStyleOptions[i];
    }
}
