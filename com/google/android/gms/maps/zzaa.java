package com.google.android.gms.maps;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class zzaa implements Creator<GoogleMapOptions> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        byte b = (byte) -1;
        byte b2 = (byte) -1;
        int i = 0;
        CameraPosition cameraPosition = null;
        byte b3 = (byte) -1;
        byte b4 = (byte) -1;
        byte b5 = (byte) -1;
        byte b6 = (byte) -1;
        byte b7 = (byte) -1;
        byte b8 = (byte) -1;
        byte b9 = (byte) -1;
        byte b10 = (byte) -1;
        byte b11 = (byte) -1;
        Float f = null;
        Float f2 = null;
        LatLngBounds latLngBounds = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    b = zzbfn.zze(parcel, readInt);
                    break;
                case 3:
                    b2 = zzbfn.zze(parcel, readInt);
                    break;
                case 4:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    cameraPosition = (CameraPosition) zzbfn.zza(parcel, readInt, CameraPosition.CREATOR);
                    break;
                case 6:
                    b3 = zzbfn.zze(parcel, readInt);
                    break;
                case 7:
                    b4 = zzbfn.zze(parcel, readInt);
                    break;
                case 8:
                    b5 = zzbfn.zze(parcel, readInt);
                    break;
                case 9:
                    b6 = zzbfn.zze(parcel, readInt);
                    break;
                case 10:
                    b7 = zzbfn.zze(parcel, readInt);
                    break;
                case 11:
                    b8 = zzbfn.zze(parcel, readInt);
                    break;
                case 12:
                    b9 = zzbfn.zze(parcel, readInt);
                    break;
                case 14:
                    b10 = zzbfn.zze(parcel, readInt);
                    break;
                case 15:
                    b11 = zzbfn.zze(parcel, readInt);
                    break;
                case 16:
                    f = zzbfn.zzm(parcel, readInt);
                    break;
                case 17:
                    f2 = zzbfn.zzm(parcel, readInt);
                    break;
                case 18:
                    latLngBounds = (LatLngBounds) zzbfn.zza(parcel, readInt, LatLngBounds.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new GoogleMapOptions(b, b2, i, cameraPosition, b3, b4, b5, b6, b7, b8, b9, b10, b11, f, f2, latLngBounds);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new GoogleMapOptions[i];
    }
}
