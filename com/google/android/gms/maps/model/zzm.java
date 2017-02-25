package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzm implements Creator<StreetViewPanoramaCamera> {
    static void zza(StreetViewPanoramaCamera streetViewPanoramaCamera, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, streetViewPanoramaCamera.zoom);
        zzc.zza(parcel, 3, streetViewPanoramaCamera.tilt);
        zzc.zza(parcel, 4, streetViewPanoramaCamera.bearing);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhJ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlr(i);
    }

    public StreetViewPanoramaCamera zzhJ(Parcel parcel) {
        float f = 0.0f;
        int zzaY = zzb.zzaY(parcel);
        float f2 = 0.0f;
        float f3 = 0.0f;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    f3 = zzb.zzl(parcel, zzaX);
                    break;
                case 3:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 4:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new StreetViewPanoramaCamera(f3, f2, f);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public StreetViewPanoramaCamera[] zzlr(int i) {
        return new StreetViewPanoramaCamera[i];
    }
}
