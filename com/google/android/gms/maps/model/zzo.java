package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzo implements Creator<StreetViewPanoramaLocation> {
    static void zza(StreetViewPanoramaLocation streetViewPanoramaLocation, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, streetViewPanoramaLocation.links, i, false);
        zzc.zza(parcel, 3, streetViewPanoramaLocation.position, i, false);
        zzc.zza(parcel, 4, streetViewPanoramaLocation.panoId, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhL(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlt(i);
    }

    public StreetViewPanoramaLocation zzhL(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        LatLng latLng = null;
        StreetViewPanoramaLink[] streetViewPanoramaLinkArr = null;
        while (parcel.dataPosition() < zzaY) {
            LatLng latLng2;
            StreetViewPanoramaLink[] streetViewPanoramaLinkArr2;
            String str2;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    String str3 = str;
                    latLng2 = latLng;
                    streetViewPanoramaLinkArr2 = (StreetViewPanoramaLink[]) zzb.zzb(parcel, zzaX, StreetViewPanoramaLink.CREATOR);
                    str2 = str3;
                    break;
                case 3:
                    streetViewPanoramaLinkArr2 = streetViewPanoramaLinkArr;
                    LatLng latLng3 = (LatLng) zzb.zza(parcel, zzaX, LatLng.CREATOR);
                    str2 = str;
                    latLng2 = latLng3;
                    break;
                case 4:
                    str2 = zzb.zzq(parcel, zzaX);
                    latLng2 = latLng;
                    streetViewPanoramaLinkArr2 = streetViewPanoramaLinkArr;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    str2 = str;
                    latLng2 = latLng;
                    streetViewPanoramaLinkArr2 = streetViewPanoramaLinkArr;
                    break;
            }
            streetViewPanoramaLinkArr = streetViewPanoramaLinkArr2;
            latLng = latLng2;
            str = str2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new StreetViewPanoramaLocation(streetViewPanoramaLinkArr, latLng, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public StreetViewPanoramaLocation[] zzlt(int i) {
        return new StreetViewPanoramaLocation[i];
    }
}
