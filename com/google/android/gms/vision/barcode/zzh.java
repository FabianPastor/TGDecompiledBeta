package com.google.android.gms.vision.barcode;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

public class zzh implements Creator<GeoPoint> {
    static void zza(GeoPoint geoPoint, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, geoPoint.versionCode);
        zzb.zza(parcel, 2, geoPoint.lat);
        zzb.zza(parcel, 3, geoPoint.lng);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsl(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzaba(i);
    }

    public GeoPoint[] zzaba(int i) {
        return new GeoPoint[i];
    }

    public GeoPoint zzsl(Parcel parcel) {
        double d = 0.0d;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        double d2 = 0.0d;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    d2 = zza.zzn(parcel, zzcq);
                    break;
                case 3:
                    d = zza.zzn(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new GeoPoint(i, d2, d);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
