package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.List;

public class zzf implements Creator<WakeLockEvent> {
    static void zza(WakeLockEvent wakeLockEvent, Parcel parcel, int i) {
        int zzaV = zzc.zzaV(parcel);
        zzc.zzc(parcel, 1, wakeLockEvent.mVersionCode);
        zzc.zza(parcel, 2, wakeLockEvent.getTimeMillis());
        zzc.zza(parcel, 4, wakeLockEvent.zzyg(), false);
        zzc.zzc(parcel, 5, wakeLockEvent.zzyj());
        zzc.zzb(parcel, 6, wakeLockEvent.zzyk(), false);
        zzc.zza(parcel, 8, wakeLockEvent.zzym());
        zzc.zza(parcel, 10, wakeLockEvent.zzyh(), false);
        zzc.zzc(parcel, 11, wakeLockEvent.getEventType());
        zzc.zza(parcel, 12, wakeLockEvent.zzyl(), false);
        zzc.zza(parcel, 13, wakeLockEvent.zzyo(), false);
        zzc.zzc(parcel, 14, wakeLockEvent.zzyn());
        zzc.zza(parcel, 15, wakeLockEvent.zzyp());
        zzc.zza(parcel, 16, wakeLockEvent.zzyq());
        zzc.zza(parcel, 17, wakeLockEvent.zzyi(), false);
        zzc.zzJ(parcel, zzaV);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzbf(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzdh(i);
    }

    public WakeLockEvent zzbf(Parcel parcel) {
        int zzaU = zzb.zzaU(parcel);
        int i = 0;
        long j = 0;
        int i2 = 0;
        String str = null;
        int i3 = 0;
        List list = null;
        String str2 = null;
        long j2 = 0;
        int i4 = 0;
        String str3 = null;
        String str4 = null;
        float f = 0.0f;
        long j3 = 0;
        String str5 = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            switch (zzb.zzcW(zzaT)) {
                case 1:
                    i = zzb.zzg(parcel, zzaT);
                    break;
                case 2:
                    j = zzb.zzi(parcel, zzaT);
                    break;
                case 4:
                    str = zzb.zzq(parcel, zzaT);
                    break;
                case 5:
                    i3 = zzb.zzg(parcel, zzaT);
                    break;
                case 6:
                    list = zzb.zzE(parcel, zzaT);
                    break;
                case 8:
                    j2 = zzb.zzi(parcel, zzaT);
                    break;
                case 10:
                    str3 = zzb.zzq(parcel, zzaT);
                    break;
                case 11:
                    i2 = zzb.zzg(parcel, zzaT);
                    break;
                case 12:
                    str2 = zzb.zzq(parcel, zzaT);
                    break;
                case 13:
                    str4 = zzb.zzq(parcel, zzaT);
                    break;
                case 14:
                    i4 = zzb.zzg(parcel, zzaT);
                    break;
                case 15:
                    f = zzb.zzl(parcel, zzaT);
                    break;
                case 16:
                    j3 = zzb.zzi(parcel, zzaT);
                    break;
                case 17:
                    str5 = zzb.zzq(parcel, zzaT);
                    break;
                default:
                    zzb.zzb(parcel, zzaT);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaU) {
            return new WakeLockEvent(i, j, i2, str, i3, list, str2, j2, i4, str3, str4, f, j3, str5);
        }
        throw new zza("Overread allowed size end=" + zzaU, parcel);
    }

    public WakeLockEvent[] zzdh(int i) {
        return new WakeLockEvent[i];
    }
}
