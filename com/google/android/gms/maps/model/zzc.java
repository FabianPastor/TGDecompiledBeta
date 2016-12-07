package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;

public class zzc implements Creator<GroundOverlayOptions> {
    static void zza(GroundOverlayOptions groundOverlayOptions, Parcel parcel, int i) {
        int zzaV = com.google.android.gms.common.internal.safeparcel.zzc.zzaV(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 1, groundOverlayOptions.getVersionCode());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 2, groundOverlayOptions.zzIT(), false);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 3, groundOverlayOptions.getLocation(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 4, groundOverlayOptions.getWidth());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 5, groundOverlayOptions.getHeight());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 6, groundOverlayOptions.getBounds(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 7, groundOverlayOptions.getBearing());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 8, groundOverlayOptions.getZIndex());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 9, groundOverlayOptions.isVisible());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 10, groundOverlayOptions.getTransparency());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 11, groundOverlayOptions.getAnchorU());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 12, groundOverlayOptions.getAnchorV());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 13, groundOverlayOptions.isClickable());
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhv(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzkZ(i);
    }

    public GroundOverlayOptions zzhv(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
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
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    iBinder = zzb.zzr(parcel, zzaT);
                    break;
                case 3:
                    latLng = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 4:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                case 5:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 6:
                    latLngBounds = (LatLngBounds) zzb.zza(parcel, zzaT, LatLngBounds.CREATOR);
                    break;
                case 7:
                    f3 = zzb.zzl(parcel, zzaT);
                    break;
                case 8:
                    f4 = zzb.zzl(parcel, zzaT);
                    break;
                case 9:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 10:
                    f5 = zzb.zzl(parcel, zzaT);
                    break;
                case 11:
                    f6 = zzb.zzl(parcel, zzaT);
                    break;
                case 12:
                    f7 = zzb.zzl(parcel, zzaT);
                    break;
                case 13:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new GroundOverlayOptions(i, iBinder, latLng, f, f2, latLngBounds, f3, f4, z, f5, f6, f7, z2);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public GroundOverlayOptions[] zzkZ(int i) {
        return new GroundOverlayOptions[i];
    }
}
