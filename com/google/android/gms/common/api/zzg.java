package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zzg implements Creator<Status> {
    static void zza(Status status, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, status.getStatusCode());
        zzb.zza(parcel, 2, status.getStatusMessage(), false);
        zzb.zza(parcel, 3, status.zzarj(), i, false);
        zzb.zzc(parcel, 1000, status.mVersionCode);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzcf(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzfr(i);
    }

    public Status zzcf(Parcel parcel) {
        PendingIntent pendingIntent = null;
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    pendingIntent = (PendingIntent) zza.zza(parcel, zzcq, PendingIntent.CREATOR);
                    break;
                case 1000:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Status(i2, i, str, pendingIntent);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public Status[] zzfr(int i) {
        return new Status[i];
    }
}
