package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.List;

public class zzl implements Creator<PolylineOptions> {
    static void zza(PolylineOptions polylineOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, polylineOptions.getPoints(), false);
        zzc.zza(parcel, 3, polylineOptions.getWidth());
        zzc.zzc(parcel, 4, polylineOptions.getColor());
        zzc.zza(parcel, 5, polylineOptions.getZIndex());
        zzc.zza(parcel, 6, polylineOptions.isVisible());
        zzc.zza(parcel, 7, polylineOptions.isGeodesic());
        zzc.zza(parcel, 8, polylineOptions.isClickable());
        zzc.zza(parcel, 9, polylineOptions.getStartCap(), i, false);
        zzc.zza(parcel, 10, polylineOptions.getEndCap(), i, false);
        zzc.zzc(parcel, 11, polylineOptions.getJointType());
        zzc.zzc(parcel, 12, polylineOptions.getPattern(), false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhI(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlq(i);
    }

    public PolylineOptions zzhI(Parcel parcel) {
        float f = 0.0f;
        List list = null;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        Cap cap = null;
        Cap cap2 = null;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        int i2 = 0;
        float f2 = 0.0f;
        List list2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    list2 = zzb.zzc(parcel, zzaX, LatLng.CREATOR);
                    break;
                case 3:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 4:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 5:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                case 6:
                    z3 = zzb.zzc(parcel, zzaX);
                    break;
                case 7:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 8:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 9:
                    cap2 = (Cap) zzb.zza(parcel, zzaX, Cap.CREATOR);
                    break;
                case 10:
                    cap = (Cap) zzb.zza(parcel, zzaX, Cap.CREATOR);
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
            return new PolylineOptions(list2, f2, i2, f, z3, z2, z, cap2, cap, i, list);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public PolylineOptions[] zzlq(int i) {
        return new PolylineOptions[i];
    }
}
