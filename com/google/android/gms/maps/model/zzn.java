package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzn implements Creator<StreetViewPanoramaLink> {
    static void zza(StreetViewPanoramaLink streetViewPanoramaLink, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, streetViewPanoramaLink.panoId, false);
        zzc.zza(parcel, 3, streetViewPanoramaLink.bearing);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhK(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzls(i);
    }

    public StreetViewPanoramaLink zzhK(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        float f = 0.0f;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new StreetViewPanoramaLink(str, f);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public StreetViewPanoramaLink[] zzls(int i) {
        return new StreetViewPanoramaLink[i];
    }
}
