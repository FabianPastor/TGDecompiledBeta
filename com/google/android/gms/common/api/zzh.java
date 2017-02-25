package com.google.android.gms.common.api;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzh implements Creator<Status> {
    static void zza(Status status, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 1, status.getStatusCode());
        zzc.zza(parcel, 2, status.getStatusMessage(), false);
        zzc.zza(parcel, 3, status.zzvu(), i, false);
        zzc.zzc(parcel, 1000, status.zzaiI);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzaM(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzcz(i);
    }

    public Status zzaM(Parcel parcel) {
        PendingIntent pendingIntent = null;
        int i = 0;
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 1:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    pendingIntent = (PendingIntent) zzb.zza(parcel, zzaX, PendingIntent.CREATOR);
                    break;
                case 1000:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new Status(i2, i, str, pendingIntent);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public Status[] zzcz(int i) {
        return new Status[i];
    }
}
