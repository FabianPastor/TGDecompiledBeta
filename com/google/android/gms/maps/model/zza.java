package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zza implements Creator<CameraPosition> {
    static void zza(CameraPosition cameraPosition, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, cameraPosition.target, i, false);
        zzc.zza(parcel, 3, cameraPosition.zoom);
        zzc.zza(parcel, 4, cameraPosition.tilt);
        zzc.zza(parcel, 5, cameraPosition.bearing);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlf(i);
    }

    public CameraPosition zzhx(Parcel parcel) {
        float f = 0.0f;
        int zzaY = zzb.zzaY(parcel);
        LatLng latLng = null;
        float f2 = 0.0f;
        float f3 = 0.0f;
        while (parcel.dataPosition() < zzaY) {
            LatLng latLng2;
            float f4;
            int zzaX = zzb.zzaX(parcel);
            float f5;
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    f5 = f;
                    f = f2;
                    f2 = f3;
                    latLng2 = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    f4 = f5;
                    break;
                case 3:
                    latLng2 = latLng;
                    f5 = f2;
                    f2 = zzb.zzl(parcel, zzaX);
                    f4 = f;
                    f = f5;
                    break;
                case 4:
                    f2 = f3;
                    latLng2 = latLng;
                    f5 = f;
                    f = zzb.zzl(parcel, zzaX);
                    f4 = f5;
                    break;
                case 5:
                    f4 = zzb.zzl(parcel, zzaX);
                    f = f2;
                    f2 = f3;
                    latLng2 = latLng;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    f4 = f;
                    f = f2;
                    f2 = f3;
                    latLng2 = latLng;
                    break;
            }
            latLng = latLng2;
            f3 = f2;
            f2 = f;
            f = f4;
        }
        if (parcel.dataPosition() == zzaY) {
            return new CameraPosition(latLng, f3, f2, f);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zzb.zza("Overread allowed size end=" + zzaY, parcel);
    }

    public CameraPosition[] zzlf(int i) {
        return new CameraPosition[i];
    }
}
