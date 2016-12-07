package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzo implements Creator<Tile> {
    static void zza(Tile tile, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, tile.getVersionCode());
        zzb.zzc(parcel, 2, tile.width);
        zzb.zzc(parcel, 3, tile.height);
        zzb.zza(parcel, 4, tile.data, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpt(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwx(i);
    }

    public Tile zzpt(Parcel parcel) {
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        byte[] bArr = null;
        int i2 = 0;
        int i3 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 3:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 4:
                    bArr = zza.zzt(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Tile(i3, i2, i, bArr);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public Tile[] zzwx(int i) {
        return new Tile[i];
    }
}
