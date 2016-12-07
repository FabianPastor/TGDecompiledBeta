package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class zzb implements Creator<StreetViewPanoramaOptions> {
    static void zza(StreetViewPanoramaOptions streetViewPanoramaOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, streetViewPanoramaOptions.getVersionCode());
        zzc.zza(parcel, 2, streetViewPanoramaOptions.getStreetViewPanoramaCamera(), i, false);
        zzc.zza(parcel, 3, streetViewPanoramaOptions.getPanoramaId(), false);
        zzc.zza(parcel, 4, streetViewPanoramaOptions.getPosition(), i, false);
        zzc.zza(parcel, 5, streetViewPanoramaOptions.getRadius(), false);
        zzc.zza(parcel, 6, streetViewPanoramaOptions.zzIN());
        zzc.zza(parcel, 7, streetViewPanoramaOptions.zzIF());
        zzc.zza(parcel, 8, streetViewPanoramaOptions.zzIO());
        zzc.zza(parcel, 9, streetViewPanoramaOptions.zzIP());
        zzc.zza(parcel, 10, streetViewPanoramaOptions.zzIB());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhs(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzkW(i);
    }

    public StreetViewPanoramaOptions zzhs(Parcel parcel) {
        Integer num = null;
        byte b = (byte) 0;
        int zzaU = com.google.android.gms.common.internal.safeparcel.zzb.zzaU(parcel);
        byte b2 = (byte) 0;
        byte b3 = (byte) 0;
        byte b4 = (byte) 0;
        byte b5 = (byte) 0;
        LatLng latLng = null;
        String str = null;
        StreetViewPanoramaCamera streetViewPanoramaCamera = null;
        int i = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = com.google.android.gms.common.internal.safeparcel.zzb.zzaT(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzcW(zzaT)) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    streetViewPanoramaCamera = (StreetViewPanoramaCamera) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, StreetViewPanoramaCamera.CREATOR);
                    break;
                case 3:
                    str = com.google.android.gms.common.internal.safeparcel.zzb.zzq(parcel, zzaT);
                    break;
                case 4:
                    latLng = (LatLng) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 5:
                    num = com.google.android.gms.common.internal.safeparcel.zzb.zzh(parcel, zzaT);
                    break;
                case 6:
                    b5 = com.google.android.gms.common.internal.safeparcel.zzb.zze(parcel, zzaT);
                    break;
                case 7:
                    b4 = com.google.android.gms.common.internal.safeparcel.zzb.zze(parcel, zzaT);
                    break;
                case 8:
                    b3 = com.google.android.gms.common.internal.safeparcel.zzb.zze(parcel, zzaT);
                    break;
                case 9:
                    b2 = com.google.android.gms.common.internal.safeparcel.zzb.zze(parcel, zzaT);
                    break;
                case 10:
                    b = com.google.android.gms.common.internal.safeparcel.zzb.zze(parcel, zzaT);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new StreetViewPanoramaOptions(i, streetViewPanoramaCamera, str, latLng, num, b5, b4, b3, b2, b);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public StreetViewPanoramaOptions[] zzkW(int i) {
        return new StreetViewPanoramaOptions[i];
    }
}
