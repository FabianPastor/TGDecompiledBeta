package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzf implements Creator<LatLng> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        double d = 0.0d;
        int zzd = zzbfn.zzd(parcel);
        double d2 = 0.0d;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    d2 = zzbfn.zzn(parcel, readInt);
                    break;
                case 3:
                    d = zzbfn.zzn(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LatLng(d2, d);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LatLng[i];
    }
}
