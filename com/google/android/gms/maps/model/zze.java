package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zze implements Creator<LatLngBounds> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        LatLng latLng = null;
        LatLng latLng2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    latLng2 = (LatLng) zzbfn.zza(parcel, readInt, LatLng.CREATOR);
                    break;
                case 3:
                    latLng = (LatLng) zzbfn.zza(parcel, readInt, LatLng.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LatLngBounds(latLng2, latLng);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LatLngBounds[i];
    }
}
