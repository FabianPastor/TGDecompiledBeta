package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

public class zzh implements Creator<GeoPoint> {
    static void zza(GeoPoint geoPoint, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, geoPoint.versionCode);
        zzc.zza(parcel, 2, geoPoint.lat);
        zzc.zza(parcel, 3, geoPoint.lng);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzja(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zznp(i);
    }

    public GeoPoint zzja(Parcel parcel) {
        double d = 0.0d;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        double d2 = 0.0d;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    d2 = zzb.zzn(parcel, zzaT);
                    break;
                case 3:
                    d = zzb.zzn(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new GeoPoint(i, d2, d);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public GeoPoint[] zznp(int i) {
        return new GeoPoint[i];
    }
}
