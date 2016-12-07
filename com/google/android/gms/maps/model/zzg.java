package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class zzg implements Creator<MarkerOptions> {
    static void zza(MarkerOptions markerOptions, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, markerOptions.getVersionCode());
        zzb.zza(parcel, 2, markerOptions.getPosition(), i, false);
        zzb.zza(parcel, 3, markerOptions.getTitle(), false);
        zzb.zza(parcel, 4, markerOptions.getSnippet(), false);
        zzb.zza(parcel, 5, markerOptions.zzbsj(), false);
        zzb.zza(parcel, 6, markerOptions.getAnchorU());
        zzb.zza(parcel, 7, markerOptions.getAnchorV());
        zzb.zza(parcel, 8, markerOptions.isDraggable());
        zzb.zza(parcel, 9, markerOptions.isVisible());
        zzb.zza(parcel, 10, markerOptions.isFlat());
        zzb.zza(parcel, 11, markerOptions.getRotation());
        zzb.zza(parcel, 12, markerOptions.getInfoWindowAnchorU());
        zzb.zza(parcel, 13, markerOptions.getInfoWindowAnchorV());
        zzb.zza(parcel, 14, markerOptions.getAlpha());
        zzb.zza(parcel, 15, markerOptions.getZIndex());
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzot(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzvy(i);
    }

    public MarkerOptions zzot(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        LatLng latLng = null;
        String str = null;
        String str2 = null;
        IBinder iBinder = null;
        float f = 0.0f;
        float f2 = 0.0f;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        float f3 = 0.0f;
        float f4 = 0.5f;
        float f5 = 0.0f;
        float f6 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        float f7 = 0.0f;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    latLng = (LatLng) zza.zza(parcel, zzcp, LatLng.CREATOR);
                    break;
                case 3:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 4:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 5:
                    iBinder = zza.zzr(parcel, zzcp);
                    break;
                case 6:
                    f = zza.zzl(parcel, zzcp);
                    break;
                case 7:
                    f2 = zza.zzl(parcel, zzcp);
                    break;
                case 8:
                    z = zza.zzc(parcel, zzcp);
                    break;
                case 9:
                    z2 = zza.zzc(parcel, zzcp);
                    break;
                case 10:
                    z3 = zza.zzc(parcel, zzcp);
                    break;
                case 11:
                    f3 = zza.zzl(parcel, zzcp);
                    break;
                case 12:
                    f4 = zza.zzl(parcel, zzcp);
                    break;
                case 13:
                    f5 = zza.zzl(parcel, zzcp);
                    break;
                case 14:
                    f6 = zza.zzl(parcel, zzcp);
                    break;
                case 15:
                    f7 = zza.zzl(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new MarkerOptions(i, latLng, str, str2, iBinder, f, f2, z, z2, z3, f3, f4, f5, f6, f7);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public MarkerOptions[] zzvy(int i) {
        return new MarkerOptions[i];
    }
}
