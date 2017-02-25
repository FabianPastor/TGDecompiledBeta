package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzd implements Creator<GroundOverlayOptions> {
    static void zza(GroundOverlayOptions groundOverlayOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, groundOverlayOptions.zzJJ(), false);
        zzc.zza(parcel, 3, groundOverlayOptions.getLocation(), i, false);
        zzc.zza(parcel, 4, groundOverlayOptions.getWidth());
        zzc.zza(parcel, 5, groundOverlayOptions.getHeight());
        zzc.zza(parcel, 6, groundOverlayOptions.getBounds(), i, false);
        zzc.zza(parcel, 7, groundOverlayOptions.getBearing());
        zzc.zza(parcel, 8, groundOverlayOptions.getZIndex());
        zzc.zza(parcel, 9, groundOverlayOptions.isVisible());
        zzc.zza(parcel, 10, groundOverlayOptions.getTransparency());
        zzc.zza(parcel, 11, groundOverlayOptions.getAnchorU());
        zzc.zza(parcel, 12, groundOverlayOptions.getAnchorV());
        zzc.zza(parcel, 13, groundOverlayOptions.isClickable());
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhA(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzli(i);
    }

    public GroundOverlayOptions zzhA(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        IBinder iBinder = null;
        LatLng latLng = null;
        float f = 0.0f;
        float f2 = 0.0f;
        LatLngBounds latLngBounds = null;
        float f3 = 0.0f;
        float f4 = 0.0f;
        boolean z = false;
        float f5 = 0.0f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        boolean z2 = false;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    iBinder = zzb.zzr(parcel, zzaX);
                    break;
                case 3:
                    latLng = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    break;
                case 4:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 5:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 6:
                    latLngBounds = (LatLngBounds) zzb.zza(parcel, zzaX, LatLngBounds.CREATOR);
                    break;
                case 7:
                    f3 = zzb.zzl(parcel, zzaX);
                    break;
                case 8:
                    f4 = zzb.zzl(parcel, zzaX);
                    break;
                case 9:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 10:
                    f5 = zzb.zzl(parcel, zzaX);
                    break;
                case 11:
                    f6 = zzb.zzl(parcel, zzaX);
                    break;
                case 12:
                    f7 = zzb.zzl(parcel, zzaX);
                    break;
                case 13:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new GroundOverlayOptions(iBinder, latLng, f, f2, latLngBounds, f3, f4, z, f5, f6, f7, z2);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public GroundOverlayOptions[] zzli(int i) {
        return new GroundOverlayOptions[i];
    }
}
