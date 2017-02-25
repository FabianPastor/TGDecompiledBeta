package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzf implements Creator<LatLng> {
    static void zza(LatLng latLng, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, latLng.latitude);
        zzc.zza(parcel, 3, latLng.longitude);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhC(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlk(i);
    }

    public LatLng zzhC(Parcel parcel) {
        double d = 0.0d;
        int zzaY = zzb.zzaY(parcel);
        double d2 = 0.0d;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    d2 = zzb.zzn(parcel, zzaX);
                    break;
                case 3:
                    d = zzb.zzn(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new LatLng(d2, d);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public LatLng[] zzlk(int i) {
        return new LatLng[i];
    }
}
