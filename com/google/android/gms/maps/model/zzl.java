package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzl implements Creator<StreetViewPanoramaLink> {
    static void zza(StreetViewPanoramaLink streetViewPanoramaLink, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, streetViewPanoramaLink.getVersionCode());
        zzb.zza(parcel, 2, streetViewPanoramaLink.panoId, false);
        zzb.zza(parcel, 3, streetViewPanoramaLink.bearing);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpq(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwu(i);
    }

    public StreetViewPanoramaLink zzpq(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str = null;
        float f = 0.0f;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    f = zza.zzl(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new StreetViewPanoramaLink(i, str, f);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public StreetViewPanoramaLink[] zzwu(int i) {
        return new StreetViewPanoramaLink[i];
    }
}
