package com.google.android.gms.common.stats;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzb;
import java.util.List;

public class zzg implements Creator<WakeLockEvent> {
    static void zza(WakeLockEvent wakeLockEvent, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, wakeLockEvent.mVersionCode);
        zzb.zza(parcel, 2, wakeLockEvent.getTimeMillis());
        zzb.zza(parcel, 4, wakeLockEvent.zzaww(), false);
        zzb.zzc(parcel, 5, wakeLockEvent.zzawz());
        zzb.zzb(parcel, 6, wakeLockEvent.zzaxa(), false);
        zzb.zza(parcel, 8, wakeLockEvent.zzaws());
        zzb.zza(parcel, 10, wakeLockEvent.zzawx(), false);
        zzb.zzc(parcel, 11, wakeLockEvent.getEventType());
        zzb.zza(parcel, 12, wakeLockEvent.zzawp(), false);
        zzb.zza(parcel, 13, wakeLockEvent.zzaxc(), false);
        zzb.zzc(parcel, 14, wakeLockEvent.zzaxb());
        zzb.zza(parcel, 15, wakeLockEvent.zzaxd());
        zzb.zza(parcel, 16, wakeLockEvent.zzaxe());
        zzb.zza(parcel, 17, wakeLockEvent.zzawy(), false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzdc(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzhh(i);
    }

    public WakeLockEvent zzdc(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
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
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    j = zza.zzi(parcel, zzcp);
                    break;
                case 4:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 5:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case 6:
                    list = zza.zzae(parcel, zzcp);
                    break;
                case 8:
                    j2 = zza.zzi(parcel, zzcp);
                    break;
                case 10:
                    str3 = zza.zzq(parcel, zzcp);
                    break;
                case 11:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 12:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 13:
                    str4 = zza.zzq(parcel, zzcp);
                    break;
                case 14:
                    i4 = zza.zzg(parcel, zzcp);
                    break;
                case 15:
                    f = zza.zzl(parcel, zzcp);
                    break;
                case 16:
                    j3 = zza.zzi(parcel, zzcp);
                    break;
                case 17:
                    str5 = zza.zzq(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new WakeLockEvent(i, j, i2, str, i3, list, str2, j2, i4, str3, str4, f, j3, str5);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public WakeLockEvent[] zzhh(int i) {
        return new WakeLockEvent[i];
    }
}
