package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import java.util.List;

public final class zzc implements Creator<CircleOptions> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        List list = null;
        float f = 0.0f;
        boolean z = false;
        int zzd = zzbfn.zzd(parcel);
        double d = 0.0d;
        boolean z2 = false;
        int i = 0;
        int i2 = 0;
        float f2 = 0.0f;
        LatLng latLng = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    latLng = (LatLng) zzbfn.zza(parcel, readInt, LatLng.CREATOR);
                    break;
                case 3:
                    d = zzbfn.zzn(parcel, readInt);
                    break;
                case 4:
                    f2 = zzbfn.zzl(parcel, readInt);
                    break;
                case 5:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 6:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 7:
                    f = zzbfn.zzl(parcel, readInt);
                    break;
                case 8:
                    z2 = zzbfn.zzc(parcel, readInt);
                    break;
                case 9:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 10:
                    list = zzbfn.zzc(parcel, readInt, PatternItem.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new CircleOptions(latLng, d, f2, i2, i, f, z2, z, list);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new CircleOptions[i];
    }
}
