package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzq implements Creator<VisibleRegion> {
    static void zza(VisibleRegion visibleRegion, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, visibleRegion.getVersionCode());
        zzb.zza(parcel, 2, visibleRegion.nearLeft, i, false);
        zzb.zza(parcel, 3, visibleRegion.nearRight, i, false);
        zzb.zza(parcel, 4, visibleRegion.farLeft, i, false);
        zzb.zza(parcel, 5, visibleRegion.farRight, i, false);
        zzb.zza(parcel, 6, visibleRegion.latLngBounds, i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpv(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwz(i);
    }

    public VisibleRegion zzpv(Parcel parcel) {
        LatLngBounds latLngBounds = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        LatLng latLng = null;
        LatLng latLng2 = null;
        LatLng latLng3 = null;
        LatLng latLng4 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    latLng4 = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    break;
                case 3:
                    latLng3 = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    break;
                case 4:
                    latLng2 = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    break;
                case 5:
                    latLng = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    break;
                case 6:
                    latLngBounds = (LatLngBounds) zza.zza(parcel, zzcq, LatLngBounds.CREATOR);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new VisibleRegion(i, latLng4, latLng3, latLng2, latLng, latLngBounds);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public VisibleRegion[] zzwz(int i) {
        return new VisibleRegion[i];
    }
}
