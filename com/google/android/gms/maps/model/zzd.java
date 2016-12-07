package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzd implements Creator<LatLngBounds> {
    static void zza(LatLngBounds latLngBounds, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, latLngBounds.getVersionCode());
        zzc.zza(parcel, 2, latLngBounds.southwest, i, false);
        zzc.zza(parcel, 3, latLngBounds.northeast, i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhw(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzla(i);
    }

    public LatLngBounds zzhw(Parcel parcel) {
        LatLng latLng = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        LatLng latLng2 = null;
        while (parcel.dataPosition() < zzaU) {
            int zzg;
            LatLng latLng3;
            int zzaT = zzb.zzaT(parcel);
            LatLng latLng4;
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    latLng4 = latLng;
                    latLng = latLng2;
                    zzg = zzb.zzg(parcel, zzaT);
                    latLng3 = latLng4;
                    break;
                case 2:
                    zzg = i;
                    latLng4 = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    latLng3 = latLng;
                    latLng = latLng4;
                    break;
                case 3:
                    latLng3 = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    latLng = latLng2;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    latLng3 = latLng;
                    latLng = latLng2;
                    zzg = i;
                    break;
            }
            i = zzg;
            latLng2 = latLng;
            latLng = latLng3;
        }
        if (parcel.dataPosition() == zzaU) {
            return new LatLngBounds(i, latLng2, latLng);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public LatLngBounds[] zzla(int i) {
        return new LatLngBounds[i];
    }
}
