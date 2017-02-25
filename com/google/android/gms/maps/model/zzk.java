package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;
import java.util.List;

public class zzk implements Creator<PolygonOptions> {
    static void zza(PolygonOptions polygonOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, polygonOptions.getPoints(), false);
        zzc.zzd(parcel, 3, polygonOptions.zzJO(), false);
        zzc.zza(parcel, 4, polygonOptions.getStrokeWidth());
        zzc.zzc(parcel, 5, polygonOptions.getStrokeColor());
        zzc.zzc(parcel, 6, polygonOptions.getFillColor());
        zzc.zza(parcel, 7, polygonOptions.getZIndex());
        zzc.zza(parcel, 8, polygonOptions.isVisible());
        zzc.zza(parcel, 9, polygonOptions.isGeodesic());
        zzc.zza(parcel, 10, polygonOptions.isClickable());
        zzc.zzc(parcel, 11, polygonOptions.getStrokeJointType());
        zzc.zzc(parcel, 12, polygonOptions.getStrokePattern(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhH(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlp(i);
    }

    public PolygonOptions zzhH(Parcel parcel) {
        List list = null;
        float f = 0.0f;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        List arrayList = new ArrayList();
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        int i2 = 0;
        int i3 = 0;
        float f2 = 0.0f;
        List list2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    list2 = zzb.zzc(parcel, zzaX, LatLng.CREATOR);
                    break;
                case 3:
                    zzb.zza(parcel, zzaX, arrayList, getClass().getClassLoader());
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 5:
                    i3 = zzb.zzg(parcel, zzaX);
                    break;
                case 6:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 7:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 8:
                    z3 = zzb.zzc(parcel, zzaX);
                    break;
                case 9:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 10:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 11:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 12:
                    list = zzb.zzc(parcel, zzaX, PatternItem.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new PolygonOptions(list2, arrayList, f2, i3, i2, f, z3, z2, z, i, list);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public PolygonOptions[] zzlp(int i) {
        return new PolygonOptions[i];
    }
}
