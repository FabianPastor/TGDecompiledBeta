package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zze implements Creator<LatLng> {
    static void zza(LatLng latLng, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, latLng.getVersionCode());
        zzb.zza(parcel, 2, latLng.latitude);
        zzb.zza(parcel, 3, latLng.longitude);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpj(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwn(i);
    }

    public LatLng zzpj(Parcel parcel) {
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
            return new LatLng(i, d2, d);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public LatLng[] zzwn(int i) {
        return new LatLng[i];
    }
}
