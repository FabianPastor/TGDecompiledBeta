package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzf implements Creator<Scope> {
    static void zza(Scope scope, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, scope.mVersionCode);
        zzb.zza(parcel, 2, scope.zzari(), false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzce(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzfq(i);
    }

    public Scope zzce(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        String str = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Scope(i, str);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public Scope[] zzfq(int i) {
        return new Scope[i];
    }
}
