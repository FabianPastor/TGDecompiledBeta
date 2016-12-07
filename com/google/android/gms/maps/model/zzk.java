package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzk implements Creator<StreetViewPanoramaCamera> {
    static void zza(StreetViewPanoramaCamera streetViewPanoramaCamera, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, streetViewPanoramaCamera.getVersionCode());
        zzb.zza(parcel, 2, streetViewPanoramaCamera.zoom);
        zzb.zza(parcel, 3, streetViewPanoramaCamera.tilt);
        zzb.zza(parcel, 4, streetViewPanoramaCamera.bearing);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpp(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwt(i);
    }

    public StreetViewPanoramaCamera zzpp(Parcel parcel) {
        float f = 0.0f;
        int zzcr = zza.zzcr(parcel);
        float f2 = 0.0f;
        int i = 0;
        float f3 = 0.0f;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    f2 = zza.zzl(parcel, zzcq);
                    break;
                case 3:
                    f3 = zza.zzl(parcel, zzcq);
                    break;
                case 4:
                    f = zza.zzl(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new StreetViewPanoramaCamera(i, f2, f3, f);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public StreetViewPanoramaCamera[] zzwt(int i) {
        return new StreetViewPanoramaCamera[i];
    }
}
