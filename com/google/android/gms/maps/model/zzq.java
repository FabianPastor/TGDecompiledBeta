package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzq implements Creator<VisibleRegion> {
    static void zza(VisibleRegion visibleRegion, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, visibleRegion.getVersionCode());
        zzc.zza(parcel, 2, visibleRegion.nearLeft, i, false);
        zzc.zza(parcel, 3, visibleRegion.nearRight, i, false);
        zzc.zza(parcel, 4, visibleRegion.farLeft, i, false);
        zzc.zza(parcel, 5, visibleRegion.farRight, i, false);
        zzc.zza(parcel, 6, visibleRegion.latLngBounds, i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhJ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzln(i);
    }

    public VisibleRegion zzhJ(Parcel parcel) {
        LatLngBounds latLngBounds = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        LatLng latLng = null;
        LatLng latLng2 = null;
        LatLng latLng3 = null;
        LatLng latLng4 = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    latLng4 = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 3:
                    latLng3 = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 4:
                    latLng2 = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 5:
                    latLng = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 6:
                    latLngBounds = (LatLngBounds) zzb.zza(parcel, zzaT, LatLngBounds.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new VisibleRegion(i, latLng4, latLng3, latLng2, latLng, latLngBounds);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public VisibleRegion[] zzln(int i) {
        return new VisibleRegion[i];
    }
}
