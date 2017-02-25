package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzr implements Creator<TileOverlayOptions> {
    static void zza(TileOverlayOptions tileOverlayOptions, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, tileOverlayOptions.zzJP(), false);
        zzc.zza(parcel, 3, tileOverlayOptions.isVisible());
        zzc.zza(parcel, 4, tileOverlayOptions.getZIndex());
        zzc.zza(parcel, 5, tileOverlayOptions.getFadeIn());
        zzc.zza(parcel, 6, tileOverlayOptions.getTransparency());
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhO(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlw(i);
    }

    public TileOverlayOptions zzhO(Parcel parcel) {
        float f = 0.0f;
        int zzaY = zzb.zzaY(parcel);
        IBinder iBinder = null;
        boolean z = false;
        boolean z2 = true;
        float f2 = 0.0f;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    iBinder = zzb.zzr(parcel, zzaX);
                    break;
                case 3:
                    z = zzb.zzc(parcel, zzaX);
                    break;
                case 4:
                    f2 = zzb.zzl(parcel, zzaX);
                    break;
                case 5:
                    z2 = zzb.zzc(parcel, zzaX);
                    break;
                case 6:
                    f = zzb.zzl(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new TileOverlayOptions(iBinder, z, f2, z2, f);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public TileOverlayOptions[] zzlw(int i) {
        return new TileOverlayOptions[i];
    }
}
