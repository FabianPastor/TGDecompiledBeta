package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<SymbolBoxParcel> {
    static void zza(SymbolBoxParcel symbolBoxParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, symbolBoxParcel.versionCode);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzsz(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabq(i);
    }

    public SymbolBoxParcel[] zzabq(int i) {
        return new SymbolBoxParcel[i];
    }

    public SymbolBoxParcel zzsz(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new SymbolBoxParcel(i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }
}
