package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import java.util.List;

public class zzj implements Creator<PolylineOptions> {
    static void zza(PolylineOptions polylineOptions, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, polylineOptions.getVersionCode());
        zzb.zzc(parcel, 2, polylineOptions.getPoints(), false);
        zzb.zza(parcel, 3, polylineOptions.getWidth());
        zzb.zzc(parcel, 4, polylineOptions.getColor());
        zzb.zza(parcel, 5, polylineOptions.getZIndex());
        zzb.zza(parcel, 6, polylineOptions.isVisible());
        zzb.zza(parcel, 7, polylineOptions.isGeodesic());
        zzb.zza(parcel, 8, polylineOptions.isClickable());
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpo(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzws(i);
    }

    public PolylineOptions zzpo(Parcel parcel) {
        float f = 0.0f;
        boolean z = false;
        int zzcr = zza.zzcr(parcel);
        List list = null;
        boolean z2 = false;
        boolean z3 = false;
        int i = 0;
        float f2 = 0.0f;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    list = zza.zzc(parcel, zzcq, LatLng.CREATOR);
                    break;
                case 3:
                    f2 = zza.zzl(parcel, zzcq);
                    break;
                case 4:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 5:
                    f = zza.zzl(parcel, zzcq);
                    break;
                case 6:
                    z3 = zza.zzc(parcel, zzcq);
                    break;
                case 7:
                    z2 = zza.zzc(parcel, zzcq);
                    break;
                case 8:
                    z = zza.zzc(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new PolylineOptions(i2, list, f2, i, f, z3, z2, z);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public PolylineOptions[] zzws(int i) {
        return new PolylineOptions[i];
    }
}
