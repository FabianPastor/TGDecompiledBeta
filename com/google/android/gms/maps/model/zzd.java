package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzd implements Creator<LatLngBounds> {
    static void zza(LatLngBounds latLngBounds, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, latLngBounds.getVersionCode());
        zzb.zza(parcel, 2, latLngBounds.southwest, i, false);
        zzb.zza(parcel, 3, latLngBounds.northeast, i, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpi(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwm(i);
    }

    public LatLngBounds zzpi(Parcel parcel) {
        LatLng latLng = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        LatLng latLng2 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzg;
            LatLng latLng3;
            int zzcq = zza.zzcq(parcel);
            LatLng latLng4;
            switch (zza.zzgu(zzcq)) {
                case 1:
                    latLng4 = latLng;
                    latLng = latLng2;
                    zzg = zza.zzg(parcel, zzcq);
                    latLng3 = latLng4;
                    break;
                case 2:
                    zzg = i;
                    latLng4 = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    latLng3 = latLng;
                    latLng = latLng4;
                    break;
                case 3:
                    latLng3 = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    latLng = latLng2;
                    zzg = i;
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    latLng3 = latLng;
                    latLng = latLng2;
                    zzg = i;
                    break;
            }
            i = zzg;
            latLng2 = latLng;
            latLng = latLng3;
        }
        if (parcel.dataPosition() == zzcr) {
            return new LatLngBounds(i, latLng2, latLng);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public LatLngBounds[] zzwm(int i) {
        return new LatLngBounds[i];
    }
}
