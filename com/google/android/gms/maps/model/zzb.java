package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb implements Creator<CircleOptions> {
    static void zza(CircleOptions circleOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, circleOptions.getVersionCode());
        zzc.zza(parcel, 2, circleOptions.getCenter(), i, false);
        zzc.zza(parcel, 3, circleOptions.getRadius());
        zzc.zza(parcel, 4, circleOptions.getStrokeWidth());
        zzc.zzc(parcel, 5, circleOptions.getStrokeColor());
        zzc.zzc(parcel, 6, circleOptions.getFillColor());
        zzc.zza(parcel, 7, circleOptions.getZIndex());
        zzc.zza(parcel, 8, circleOptions.isVisible());
        zzc.zza(parcel, 9, circleOptions.isClickable());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhu(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzkY(i);
    }

    public CircleOptions zzhu(Parcel parcel) {
        float f = 0.0f;
        boolean z = false;
        int zzaU = com.google.android.gms.common.internal.safeparcel.zzb.zzaU(parcel);
        LatLng latLng = null;
        double d = 0.0d;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        float f2 = 0.0f;
        int i3 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = com.google.android.gms.common.internal.safeparcel.zzb.zzaT(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzcW(zzaT)) {
                case 1:
                    i3 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    latLng = (LatLng) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 3:
                    d = com.google.android.gms.common.internal.safeparcel.zzb.zzn(parcel, zzaT);
                    break;
                case 4:
                    f2 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, zzaT);
                    break;
                case 5:
                    i2 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 6:
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, zzaT);
                    break;
                case 7:
                    f = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, zzaT);
                    break;
                case 8:
                    z2 = com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, zzaT);
                    break;
                case 9:
                    z = com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, zzaT);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new CircleOptions(i3, latLng, d, f2, i2, i, f, z2, z);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public CircleOptions[] zzkY(int i) {
        return new CircleOptions[i];
    }
}
