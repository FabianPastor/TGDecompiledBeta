package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzg implements Creator<MarkerOptions> {
    static void zza(MarkerOptions markerOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, markerOptions.getVersionCode());
        zzc.zza(parcel, 2, markerOptions.getPosition(), i, false);
        zzc.zza(parcel, 3, markerOptions.getTitle(), false);
        zzc.zza(parcel, 4, markerOptions.getSnippet(), false);
        zzc.zza(parcel, 5, markerOptions.zzIV(), false);
        zzc.zza(parcel, 6, markerOptions.getAnchorU());
        zzc.zza(parcel, 7, markerOptions.getAnchorV());
        zzc.zza(parcel, 8, markerOptions.isDraggable());
        zzc.zza(parcel, 9, markerOptions.isVisible());
        zzc.zza(parcel, 10, markerOptions.isFlat());
        zzc.zza(parcel, 11, markerOptions.getRotation());
        zzc.zza(parcel, 12, markerOptions.getInfoWindowAnchorU());
        zzc.zza(parcel, 13, markerOptions.getInfoWindowAnchorV());
        zzc.zza(parcel, 14, markerOptions.getAlpha());
        zzc.zza(parcel, 15, markerOptions.getZIndex());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzld(i);
    }

    public MarkerOptions zzhz(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        LatLng latLng = null;
        String str = null;
        String str2 = null;
        IBinder iBinder = null;
        float f = 0.0f;
        float f2 = 0.0f;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        float f3 = 0.0f;
        float f4 = 0.5f;
        float f5 = 0.0f;
        float f6 = 1.0f;
        float f7 = 0.0f;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    latLng = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                case 4:
                    str2 = zzb.zzq(parcel, zzaT);
                    break;
                case 5:
                    iBinder = zzb.zzr(parcel, zzaT);
                    break;
                case 6:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                case 7:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 9:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 10:
                    z3 = zzb.zzc(parcel, zzaT);
                    break;
                case 11:
                    f3 = zzb.zzl(parcel, zzaT);
                    break;
                case 12:
                    f4 = zzb.zzl(parcel, zzaT);
                    break;
                case 13:
                    f5 = zzb.zzl(parcel, zzaT);
                    break;
                case 14:
                    f6 = zzb.zzl(parcel, zzaT);
                    break;
                case 15:
                    f7 = zzb.zzl(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new MarkerOptions(i, latLng, str, str2, iBinder, f, f2, z, z2, z3, f3, f4, f5, f6, f7);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public MarkerOptions[] zzld(int i) {
        return new MarkerOptions[i];
    }
}
