package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzq implements Creator<Tile> {
    static void zza(Tile tile, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, tile.width);
        zzc.zzc(parcel, 3, tile.height);
        zzc.zza(parcel, 4, tile.data, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzhN(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzlv(i);
    }

    public Tile zzhN(Parcel parcel) {
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        byte[] bArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 4:
                    bArr = zzb.zzt(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new Tile(i2, i, bArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public Tile[] zzlv(int i) {
        return new Tile[i];
    }
}
