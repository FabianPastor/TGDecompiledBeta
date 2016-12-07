package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzk implements Creator<EventParcel> {
    static void zza(EventParcel eventParcel, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, eventParcel.versionCode);
        zzb.zza(parcel, 2, eventParcel.name, false);
        zzb.zza(parcel, 3, eventParcel.arJ, i, false);
        zzb.zza(parcel, 4, eventParcel.arK, false);
        zzb.zza(parcel, 5, eventParcel.arL);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpy(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzxd(i);
    }

    public EventParcel zzpy(Parcel parcel) {
        String str = null;
        int zzcr = zza.zzcr(parcel);
        int i = 0;
        long j = 0;
        EventParams eventParams = null;
        String str2 = null;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    eventParams = (EventParams) zza.zza(parcel, zzcq, EventParams.CREATOR);
                    break;
                case 4:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 5:
                    j = zza.zzi(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new EventParcel(i, str2, eventParams, str, j);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public EventParcel[] zzxd(int i) {
        return new EventParcel[i];
    }
}
