package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze implements Creator<LatLng> {
    static void zza(LatLng latLng, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, latLng.getVersionCode());
        zzc.zza(parcel, 2, latLng.latitude);
        zzc.zza(parcel, 3, latLng.longitude);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlb(i);
    }

    public LatLng zzhx(Parcel parcel) {
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
            return new LatLng(i, d2, d);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public LatLng[] zzlb(int i) {
        return new LatLng[i];
    }
}
