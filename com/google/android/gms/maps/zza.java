package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public class zza implements Creator<GoogleMapOptions> {
    static void zza(GoogleMapOptions googleMapOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, googleMapOptions.getVersionCode());
        zzc.zza(parcel, 2, googleMapOptions.zzIA());
        zzc.zza(parcel, 3, googleMapOptions.zzIB());
        zzc.zzc(parcel, 4, googleMapOptions.getMapType());
        zzc.zza(parcel, 5, googleMapOptions.getCamera(), i, false);
        zzc.zza(parcel, 6, googleMapOptions.zzIC());
        zzc.zza(parcel, 7, googleMapOptions.zzID());
        zzc.zza(parcel, 8, googleMapOptions.zzIE());
        zzc.zza(parcel, 9, googleMapOptions.zzIF());
        zzc.zza(parcel, 10, googleMapOptions.zzIG());
        zzc.zza(parcel, 11, googleMapOptions.zzIH());
        zzc.zza(parcel, 12, googleMapOptions.zzII());
        zzc.zza(parcel, 14, googleMapOptions.zzIJ());
        zzc.zza(parcel, 15, googleMapOptions.zzIK());
        zzc.zza(parcel, 16, googleMapOptions.getMinZoomPreference(), false);
        zzc.zza(parcel, 17, googleMapOptions.getMaxZoomPreference(), false);
        zzc.zza(parcel, 18, googleMapOptions.getLatLngBoundsForCameraTarget(), i, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhr(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzkV(i);
    }

    public GoogleMapOptions zzhr(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        byte b = (byte) -1;
        byte b2 = (byte) -1;
        int i2 = 0;
        CameraPosition cameraPosition = null;
        byte b3 = (byte) -1;
        byte b4 = (byte) -1;
        byte b5 = (byte) -1;
        byte b6 = (byte) -1;
        byte b7 = (byte) -1;
        byte b8 = (byte) -1;
        byte b9 = (byte) -1;
        byte b10 = (byte) -1;
        byte b11 = (byte) -1;
        Float f = null;
        Float f2 = null;
        LatLngBounds latLngBounds = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    b = zzb.zze(parcel, zzaT);
                    break;
                case 3:
                    b2 = zzb.zze(parcel, zzaT);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 5:
                    cameraPosition = (CameraPosition) zzb.zza(parcel, zzaT, CameraPosition.CREATOR);
                    break;
                case 6:
                    b3 = zzb.zze(parcel, zzaT);
                    break;
                case 7:
                    b4 = zzb.zze(parcel, zzaT);
                    break;
                case 8:
                    b5 = zzb.zze(parcel, zzaT);
                    break;
                case 9:
                    b6 = zzb.zze(parcel, zzaT);
                    break;
                case 10:
                    b7 = zzb.zze(parcel, zzaT);
                    break;
                case 11:
                    b8 = zzb.zze(parcel, zzaT);
                    break;
                case 12:
                    b9 = zzb.zze(parcel, zzaT);
                    break;
                case 14:
                    b10 = zzb.zze(parcel, zzaT);
                    break;
                case 15:
                    b11 = zzb.zze(parcel, zzaT);
                    break;
                case 16:
                    f = zzb.zzm(parcel, zzaT);
                    break;
                case 17:
                    f2 = zzb.zzm(parcel, zzaT);
                    break;
                case 18:
                    latLngBounds = (LatLngBounds) zzb.zza(parcel, zzaT, LatLngBounds.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new GoogleMapOptions(i, b, b2, i2, cameraPosition, b3, b4, b5, b6, b7, b8, b9, b10, b11, f, f2, latLngBounds);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaU, parcel);
    }

    public GoogleMapOptions[] zzkV(int i) {
        return new GoogleMapOptions[i];
    }
}
