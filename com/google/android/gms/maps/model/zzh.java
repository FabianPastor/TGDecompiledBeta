package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzh implements Creator<PointOfInterest> {
    static void zza(PointOfInterest pointOfInterest, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, pointOfInterest.getVersionCode());
        zzb.zza(parcel, 2, pointOfInterest.latLng, i, false);
        zzb.zza(parcel, 3, pointOfInterest.placeId, false);
        zzb.zza(parcel, 4, pointOfInterest.name, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpm(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwq(i);
    }

    public PointOfInterest zzpm(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str2 = null;
        LatLng latLng = null;
        while (parcel.dataPosition() < zzcr) {
            LatLng latLng2;
            int zzg;
            String str3;
            int zzcq = zza.zzcq(parcel);
            String str4;
            switch (zza.zzgu(zzcq)) {
                case 1:
                    str4 = str;
                    str = str2;
                    latLng2 = latLng;
                    zzg = zza.zzg(parcel, zzcq);
                    str3 = str4;
                    break;
                case 2:
                    zzg = i;
                    str4 = str2;
                    latLng2 = (LatLng) zza.zza(parcel, zzcq, LatLng.CREATOR);
                    str3 = str;
                    str = str4;
                    break;
                case 3:
                    latLng2 = latLng;
                    zzg = i;
                    str4 = str;
                    str = zza.zzq(parcel, zzcq);
                    str3 = str4;
                    break;
                case 4:
                    str3 = zza.zzq(parcel, zzcq);
                    str = str2;
                    latLng2 = latLng;
                    zzg = i;
                    break;
                default:
                    zza.zzb(parcel, zzcq);
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
        if (parcel.dataPosition() == zzcr) {
            return new PointOfInterest(i, latLng, str2, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public PointOfInterest[] zzwq(int i) {
        return new PointOfInterest[i];
    }
}
