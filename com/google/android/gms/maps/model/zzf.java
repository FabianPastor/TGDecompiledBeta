package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<MapStyleOptions> {
    static void zza(MapStyleOptions mapStyleOptions, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, mapStyleOptions.getVersionCode());
        zzb.zza(parcel, 2, mapStyleOptions.zzbsy(), false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpk(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwo(i);
    }

    public MapStyleOptions zzpk(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new MapStyleOptions(i, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public MapStyleOptions[] zzwo(int i) {
        return new MapStyleOptions[i];
    }
}
