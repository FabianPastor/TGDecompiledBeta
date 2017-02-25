package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzj implements Creator<PointOfInterest> {
    static void zza(PointOfInterest pointOfInterest, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, pointOfInterest.latLng, i, false);
        zzc.zza(parcel, 3, pointOfInterest.placeId, false);
        zzc.zza(parcel, 4, pointOfInterest.name, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhG(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlo(i);
    }

    public PointOfInterest zzhG(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        String str2 = null;
        LatLng latLng = null;
        while (parcel.dataPosition() < zzaY) {
            LatLng latLng2;
            String str3;
            int zzaX = zzb.zzaX(parcel);
            String str4;
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str4 = str;
                    str = str2;
                    latLng2 = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    str3 = str4;
                    break;
                case 3:
                    latLng2 = latLng;
                    str4 = zzb.zzq(parcel, zzaX);
                    str3 = str;
                    str = str4;
                    break;
                case 4:
                    str3 = zzb.zzq(parcel, zzaX);
                    str = str2;
                    latLng2 = latLng;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    str3 = str;
                    str = str2;
                    latLng2 = latLng;
                    break;
            }
            latLng = latLng2;
            str2 = str;
            str = str3;
        }
        if (parcel.dataPosition() == zzaY) {
            return new PointOfInterest(latLng, str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public PointOfInterest[] zzlo(int i) {
        return new PointOfInterest[i];
    }
}
