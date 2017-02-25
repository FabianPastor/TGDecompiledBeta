package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import java.util.List;

public class zzc implements Creator<CircleOptions> {
    static void zza(CircleOptions circleOptions, Parcel parcel, int i) {
        int zzaZ = com.google.android.gms.common.internal.safeparcel.zzc.zzaZ(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 2, circleOptions.getCenter(), i, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 3, circleOptions.getRadius());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 4, circleOptions.getStrokeWidth());
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 5, circleOptions.getStrokeColor());
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 6, circleOptions.getFillColor());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 7, circleOptions.getZIndex());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 8, circleOptions.isVisible());
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 9, circleOptions.isClickable());
        com.google.android.gms.common.internal.safeparcel.zzc.zzc(parcel, 10, circleOptions.getStrokePattern(), false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlh(i);
    }

    public CircleOptions zzhz(Parcel parcel) {
        List list = null;
        float f = 0.0f;
        boolean z = false;
        int zzaY = zzb.zzaY(parcel);
        double d = 0.0d;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        float f2 = 0.0f;
        LatLng latLng = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    latLng = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    break;
                case 3:
                    d = zzb.zzn(parcel, zzaX);
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 5:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 6:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 7:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 8:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 9:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 10:
                    list = zzb.zzc(parcel, zzaX, PatternItem.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new CircleOptions(latLng, d, f2, i2, i, f, z2, z, list);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public CircleOptions[] zzlh(int i) {
        return new CircleOptions[i];
    }
}
