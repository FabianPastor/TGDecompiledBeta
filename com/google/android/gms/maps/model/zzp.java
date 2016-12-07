package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzp implements Creator<TileOverlayOptions> {
    static void zza(TileOverlayOptions tileOverlayOptions, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, tileOverlayOptions.getVersionCode());
        zzc.zza(parcel, 2, tileOverlayOptions.zzIX(), false);
        zzc.zza(parcel, 3, tileOverlayOptions.isVisible());
        zzc.zza(parcel, 4, tileOverlayOptions.getZIndex());
        zzc.zza(parcel, 5, tileOverlayOptions.getFadeIn());
        zzc.zza(parcel, 6, tileOverlayOptions.getTransparency());
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhI(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlm(i);
    }

    public TileOverlayOptions zzhI(Parcel parcel) {
        boolean z = false;
        float f = 0.0f;
        int zzaU = zzb.zzaU(parcel);
        IBinder iBinder = null;
        boolean z2 = true;
        float f2 = 0.0f;
        int i = 0;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    iBinder = zzb.zzr(parcel, zzaT);
                    break;
                case 3:
                    z = zzb.zzc(parcel, zzaT);
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaT);
                    break;
                case 5:
                    z2 = zzb.zzc(parcel, zzaT);
                    break;
                case 6:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new TileOverlayOptions(i, iBinder, z, f2, z2, f);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public TileOverlayOptions[] zzlm(int i) {
        return new TileOverlayOptions[i];
    }
}
