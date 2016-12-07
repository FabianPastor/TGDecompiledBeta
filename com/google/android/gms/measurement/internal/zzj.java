package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzj implements Creator<EventParams> {
    static void zza(EventParams eventParams, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, eventParams.versionCode);
        zzb.zza(parcel, 2, eventParams.zzbww(), false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzxc(i);
    }

    public EventParams zzpx(Parcel parcel) {
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        Bundle bundle = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    bundle = zza.zzs(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new EventParams(i, bundle);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public EventParams[] zzxc(int i) {
        return new EventParams[i];
    }
}
