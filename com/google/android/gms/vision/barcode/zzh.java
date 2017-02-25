package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

public class zzh implements Creator<GeoPoint> {
    static void zza(GeoPoint geoPoint, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, geoPoint.versionCode);
        zzc.zza(parcel, 2, geoPoint.lat);
        zzc.zza(parcel, 3, geoPoint.lng);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzjG(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoa(i);
    }

    public GeoPoint zzjG(Parcel parcel) {
        double d = 0.0d;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        double d2 = 0.0d;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    d2 = zzb.zzn(parcel, zzaX);
                    break;
                case 3:
                    d = zzb.zzn(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new GeoPoint(i, d2, d);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public GeoPoint[] zzoa(int i) {
        return new GeoPoint[i];
    }
}
