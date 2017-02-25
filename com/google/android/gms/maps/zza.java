package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public class zza implements Creator<GoogleMapOptions> {
    static void zza(GoogleMapOptions googleMapOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, googleMapOptions.zzJn());
        zzc.zza(parcel, 3, googleMapOptions.zzJo());
        zzc.zzc(parcel, 4, googleMapOptions.getMapType());
        zzc.zza(parcel, 5, googleMapOptions.getCamera(), i, false);
        zzc.zza(parcel, 6, googleMapOptions.zzJp());
        zzc.zza(parcel, 7, googleMapOptions.zzJq());
        zzc.zza(parcel, 8, googleMapOptions.zzJr());
        zzc.zza(parcel, 9, googleMapOptions.zzJs());
        zzc.zza(parcel, 10, googleMapOptions.zzJt());
        zzc.zza(parcel, 11, googleMapOptions.zzJu());
        zzc.zza(parcel, 12, googleMapOptions.zzJv());
        zzc.zza(parcel, 14, googleMapOptions.zzJw());
        zzc.zza(parcel, 15, googleMapOptions.zzJx());
        zzc.zza(parcel, 16, googleMapOptions.getMinZoomPreference(), false);
        zzc.zza(parcel, 17, googleMapOptions.getMaxZoomPreference(), false);
        zzc.zza(parcel, 18, googleMapOptions.getLatLngBoundsForCameraTarget(), i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhv(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzld(i);
    }

    public GoogleMapOptions zzhv(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        byte b = (byte) -1;
        byte b2 = (byte) -1;
        int i = 0;
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
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    b = zzb.zze(parcel, zzaX);
                    break;
                case 3:
                    b2 = zzb.zze(parcel, zzaX);
                    break;
                case 4:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 5:
                    cameraPosition = (CameraPosition) zzb.zza(parcel, zzaX, CameraPosition.CREATOR);
                    break;
                case 6:
                    b3 = zzb.zze(parcel, zzaX);
                    break;
                case 7:
                    b4 = zzb.zze(parcel, zzaX);
                    break;
                case 8:
                    b5 = zzb.zze(parcel, zzaX);
                    break;
                case 9:
                    b6 = zzb.zze(parcel, zzaX);
                    break;
                case 10:
                    b7 = zzb.zze(parcel, zzaX);
                    break;
                case 11:
                    b8 = zzb.zze(parcel, zzaX);
                    break;
                case 12:
                    b9 = zzb.zze(parcel, zzaX);
                    break;
                case 14:
                    b10 = zzb.zze(parcel, zzaX);
                    break;
                case 15:
                    b11 = zzb.zze(parcel, zzaX);
                    break;
                case 16:
                    f = zzb.zzm(parcel, zzaX);
                    break;
                case 17:
                    f2 = zzb.zzm(parcel, zzaX);
                    break;
                case 18:
                    latLngBounds = (LatLngBounds) zzb.zza(parcel, zzaX, LatLngBounds.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new GoogleMapOptions(b, b2, i, cameraPosition, b3, b4, b5, b6, b7, b8, b9, b10, b11, f, f2, latLngBounds);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public GoogleMapOptions[] zzld(int i) {
        return new GoogleMapOptions[i];
    }
}
