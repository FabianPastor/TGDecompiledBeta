package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.List;

public class zzj implements Creator<PolylineOptions> {
    static void zza(PolylineOptions polylineOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, polylineOptions.getVersionCode());
        zzc.zzc(parcel, 2, polylineOptions.getPoints(), false);
        zzc.zza(parcel, 3, polylineOptions.getWidth());
        zzc.zzc(parcel, 4, polylineOptions.getColor());
        zzc.zza(parcel, 5, polylineOptions.getZIndex());
        zzc.zza(parcel, 6, polylineOptions.isVisible());
        zzc.zza(parcel, 7, polylineOptions.isGeodesic());
        zzc.zza(parcel, 8, polylineOptions.isClickable());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhC(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlg(i);
    }

    public PolylineOptions zzhC(Parcel parcel) {
        float f = 0.0f;
        boolean z = false;
        int zzaU = zzb.zzaU(parcel);
        List list = null;
        boolean z2 = false;
        boolean z3 = false;
        int i = 0;
        float f2 = 0.0f;
        int i2 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    list = zzb.zzc(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 3:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 4:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 5:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                case 6:
                    z3 = zzb.zzc(parcel, zzaT);
                    break;
                case 7:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new PolylineOptions(i2, list, f2, i, f, z3, z2, z);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public PolylineOptions[] zzlg(int i) {
        return new PolylineOptions[i];
    }
}
