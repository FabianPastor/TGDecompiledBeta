package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<MapStyleOptions> {
    static void zza(MapStyleOptions mapStyleOptions, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, mapStyleOptions.getVersionCode());
        zzb.zza(parcel, 2, mapStyleOptions.zzbsi(), false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzos(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzvx(i);
    }

    public MapStyleOptions zzos(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new MapStyleOptions(i, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public MapStyleOptions[] zzvx(int i) {
        return new MapStyleOptions[i];
    }
}
