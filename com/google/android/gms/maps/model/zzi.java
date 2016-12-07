package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;
import java.util.List;

public class zzi implements Creator<PolygonOptions> {
    static void zza(PolygonOptions polygonOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, polygonOptions.getVersionCode());
        zzc.zzc(parcel, 2, polygonOptions.getPoints(), false);
        zzc.zzd(parcel, 3, polygonOptions.zzIW(), false);
        zzc.zza(parcel, 4, polygonOptions.getStrokeWidth());
        zzc.zzc(parcel, 5, polygonOptions.getStrokeColor());
        zzc.zzc(parcel, 6, polygonOptions.getFillColor());
        zzc.zza(parcel, 7, polygonOptions.getZIndex());
        zzc.zza(parcel, 8, polygonOptions.isVisible());
        zzc.zza(parcel, 9, polygonOptions.isGeodesic());
        zzc.zza(parcel, 10, polygonOptions.isClickable());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhB(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlf(i);
    }

    public PolygonOptions zzhB(Parcel parcel) {
        float f = 0.0f;
        boolean z = false;
        int zzaU = zzb.zzaU(parcel);
        List list = null;
        List arrayList = new ArrayList();
        boolean z2 = false;
        boolean z3 = false;
        int i = 0;
        int i2 = 0;
        float f2 = 0.0f;
        int i3 = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    list = zzb.zzc(parcel, zzaT, LatLng.CREATOR);
                    break;
                case 3:
                    zzb.zza(parcel, zzaT, arrayList, getClass().getClassLoader());
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 5:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 6:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 7:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                case 8:
                    z3 = zzb.zzc(parcel, zzaT);
                    break;
                case 9:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 10:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new PolygonOptions(i3, list, arrayList, f2, i2, i, f, z3, z2, z);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public PolygonOptions[] zzlf(int i) {
        return new PolygonOptions[i];
    }
}
