package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzn implements Creator<StreetViewPanoramaOrientation> {
    static void zza(StreetViewPanoramaOrientation streetViewPanoramaOrientation, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, streetViewPanoramaOrientation.getVersionCode());
        zzc.zza(parcel, 2, streetViewPanoramaOrientation.tilt);
        zzc.zza(parcel, 3, streetViewPanoramaOrientation.bearing);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhG(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlk(i);
    }

    public StreetViewPanoramaOrientation zzhG(Parcel parcel) {
        float f = 0.0f;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        float f2 = 0.0f;
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
                    f = zzb.zzl(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new StreetViewPanoramaOrientation(i, f2, f);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public StreetViewPanoramaOrientation[] zzlk(int i) {
        return new StreetViewPanoramaOrientation[i];
    }
}
