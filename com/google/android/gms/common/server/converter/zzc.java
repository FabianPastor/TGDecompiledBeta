package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.server.converter.StringToIntConverter.Entry;

public class zzc implements Creator<Entry> {
    static void zza(Entry entry, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, entry.versionCode);
        zzb.zza(parcel, 2, entry.Fe, false);
        zzb.zzc(parcel, 3, entry.Ff);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzcw(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzgz(i);
    }

    public Entry zzcw(Parcel parcel) {
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    i = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Entry(i2, str, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public Entry[] zzgz(int i) {
        return new Entry[i];
    }
}
