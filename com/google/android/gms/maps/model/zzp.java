package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzp implements Creator<TileOverlayOptions> {
    static void zza(TileOverlayOptions tileOverlayOptions, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, tileOverlayOptions.getVersionCode());
        zzb.zza(parcel, 2, tileOverlayOptions.zzbtb(), false);
        zzb.zza(parcel, 3, tileOverlayOptions.isVisible());
        zzb.zza(parcel, 4, tileOverlayOptions.getZIndex());
        zzb.zza(parcel, 5, tileOverlayOptions.getFadeIn());
        zzb.zza(parcel, 6, tileOverlayOptions.getTransparency());
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpu(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwy(i);
    }

    public TileOverlayOptions zzpu(Parcel parcel) {
        boolean z = false;
        float f = 0.0f;
        int zzcr = zza.zzcr(parcel);
        IBinder iBinder = null;
        boolean z2 = true;
        float f2 = 0.0f;
        int i = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    iBinder = zza.zzr(parcel, zzcq);
                    break;
                case 3:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 4:
                    f2 = zza.zzl(parcel, zzcq);
                    break;
                case 5:
                    z2 = zza.zzc(parcel, zzcq);
                    break;
                case 6:
                    f = zza.zzl(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new TileOverlayOptions(i, iBinder, z, f2, z2, f);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public TileOverlayOptions[] zzwy(int i) {
        return new TileOverlayOptions[i];
    }
}
