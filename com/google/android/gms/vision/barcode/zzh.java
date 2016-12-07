package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

public class zzh implements Creator<GeoPoint> {
    static void zza(GeoPoint geoPoint, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, geoPoint.versionCode);
        zzb.zza(parcel, 2, geoPoint.lat);
        zzb.zza(parcel, 3, geoPoint.lng);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsv(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabk(i);
    }

    public GeoPoint[] zzabk(int i) {
        return new GeoPoint[i];
    }

    public GeoPoint zzsv(Parcel parcel) {
        double d = 0.0d;
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        double d2 = 0.0d;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    d2 = zza.zzn(parcel, zzcp);
                    break;
                case 3:
                    d = zza.zzn(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new GeoPoint(i, d2, d);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
