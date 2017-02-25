package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzp implements Creator<StreetViewPanoramaOrientation> {
    static void zza(StreetViewPanoramaOrientation streetViewPanoramaOrientation, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, streetViewPanoramaOrientation.tilt);
        zzc.zza(parcel, 3, streetViewPanoramaOrientation.bearing);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhM(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlu(i);
    }

    public StreetViewPanoramaOrientation zzhM(Parcel parcel) {
        float f = 0.0f;
        int zzaY = zzb.zzaY(parcel);
        float f2 = 0.0f;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 3:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new StreetViewPanoramaOrientation(f2, f);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public StreetViewPanoramaOrientation[] zzlu(int i) {
        return new StreetViewPanoramaOrientation[i];
    }
}
