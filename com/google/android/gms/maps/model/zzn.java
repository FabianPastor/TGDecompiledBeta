package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzn implements Creator<StreetViewPanoramaOrientation> {
    static void zza(StreetViewPanoramaOrientation streetViewPanoramaOrientation, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, streetViewPanoramaOrientation.getVersionCode());
        zzb.zza(parcel, 2, streetViewPanoramaOrientation.tilt);
        zzb.zza(parcel, 3, streetViewPanoramaOrientation.bearing);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzps(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzww(i);
    }

    public StreetViewPanoramaOrientation zzps(Parcel parcel) {
        float f = 0.0f;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        float f2 = 0.0f;
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
                    f = zza.zzl(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new StreetViewPanoramaOrientation(i, f2, f);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public StreetViewPanoramaOrientation[] zzww(int i) {
        return new StreetViewPanoramaOrientation[i];
    }
}
