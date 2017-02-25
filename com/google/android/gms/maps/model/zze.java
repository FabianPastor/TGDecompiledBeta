package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze implements Creator<LatLngBounds> {
    static void zza(LatLngBounds latLngBounds, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, latLngBounds.southwest, i, false);
        zzc.zza(parcel, 3, latLngBounds.northeast, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhB(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlj(i);
    }

    public LatLngBounds zzhB(Parcel parcel) {
        LatLng latLng = null;
        int zzaY = zzb.zzaY(parcel);
        LatLng latLng2 = null;
        while (parcel.dataPosition() < zzaY) {
            LatLng latLng3;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    LatLng latLng4 = latLng;
                    latLng = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    latLng3 = latLng4;
                    break;
                case 3:
                    latLng3 = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    latLng = latLng2;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    latLng3 = latLng;
                    latLng = latLng2;
                    break;
            }
            latLng2 = latLng;
            latLng = latLng3;
        }
        if (parcel.dataPosition() == zzaY) {
            return new LatLngBounds(latLng2, latLng);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public LatLngBounds[] zzlj(int i) {
        return new LatLngBounds[i];
    }
}
