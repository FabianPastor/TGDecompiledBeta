package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zza implements Creator<CameraPosition> {
    static void zza(CameraPosition cameraPosition, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, cameraPosition.getVersionCode());
        zzc.zza(parcel, 2, cameraPosition.target, i, false);
        zzc.zza(parcel, 3, cameraPosition.zoom);
        zzc.zza(parcel, 4, cameraPosition.tilt);
        zzc.zza(parcel, 5, cameraPosition.bearing);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzht(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzkX(i);
    }

    public CameraPosition zzht(Parcel parcel) {
        float f = 0.0f;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        LatLng latLng = null;
        float f2 = 0.0f;
        float f3 = 0.0f;
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
                    f3 = zzb.zzl(parcel, zzaT);
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 5:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new CameraPosition(i, latLng, f3, f2, f);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaU, parcel);
    }

    public CameraPosition[] zzkX(int i) {
        return new CameraPosition[i];
    }
}
