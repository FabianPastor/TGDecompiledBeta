package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzh implements Creator<PointOfInterest> {
    static void zza(PointOfInterest pointOfInterest, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, pointOfInterest.getVersionCode());
        zzc.zza(parcel, 2, pointOfInterest.latLng, i, false);
        zzc.zza(parcel, 3, pointOfInterest.placeId, false);
        zzc.zza(parcel, 4, pointOfInterest.name, false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhA(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzle(i);
    }

    public PointOfInterest zzhA(Parcel parcel) {
        String str = null;
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        String str2 = null;
        LatLng latLng = null;
        while (parcel.dataPosition() < zzaU) {
            LatLng latLng2;
            int zzg;
            String str3;
            int zzaT = zzb.zzaT(parcel);
            String str4;
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    str4 = str;
                    str = str2;
                    latLng2 = latLng;
                    zzg = zzb.zzg(parcel, zzaT);
                    str3 = str4;
                    break;
                case 2:
                    zzg = i;
                    str4 = str2;
                    latLng2 = (LatLng) zzb.zza(parcel, zzaT, LatLng.CREATOR);
                    str3 = str;
                    str = str4;
                    break;
                case 3:
                    latLng2 = latLng;
                    zzg = i;
                    str4 = str;
                    str = zzb.zzq(parcel, zzaT);
                    str3 = str4;
                    break;
                case 4:
                    str3 = zzb.zzq(parcel, zzaT);
                    str = str2;
                    latLng2 = latLng;
                    zzg = i;
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    str3 = str;
                    str = str2;
                    latLng2 = latLng;
                    zzg = i;
                    break;
            }
            i = zzg;
            latLng = latLng2;
            str2 = str;
            str = str3;
        }
        if (parcel.dataPosition() == zzaU) {
            return new PointOfInterest(i, latLng, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public PointOfInterest[] zzle(int i) {
        return new PointOfInterest[i];
    }
}
