package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzk implements Creator<StreetViewPanoramaCamera> {
    static void zza(StreetViewPanoramaCamera streetViewPanoramaCamera, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, streetViewPanoramaCamera.getVersionCode());
        zzc.zza(parcel, 2, streetViewPanoramaCamera.zoom);
        zzc.zza(parcel, 3, streetViewPanoramaCamera.tilt);
        zzc.zza(parcel, 4, streetViewPanoramaCamera.bearing);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhD(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlh(i);
    }

    public StreetViewPanoramaCamera zzhD(Parcel parcel) {
        float f = 0.0f;
        int zzaU = zzb.zzaU(parcel);
        float f2 = 0.0f;
        int i = 0;
        float f3 = 0.0f;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 3:
                    f3 = zzb.zzl(parcel, zzaT);
                    break;
                case 4:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new StreetViewPanoramaCamera(i, f2, f3, f);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public StreetViewPanoramaCamera[] zzlh(int i) {
        return new StreetViewPanoramaCamera[i];
    }
}
