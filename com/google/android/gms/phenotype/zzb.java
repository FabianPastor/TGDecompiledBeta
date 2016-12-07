package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzb implements Creator<Flag> {
    static void zza(Flag flag, Parcel parcel, int i) {
        int zzcr = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, flag.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, flag.name, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, flag.axp);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, flag.afy);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, flag.afA);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, flag.Dr, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, flag.axq, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 8, flag.axr);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 9, flag.axs);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzrr(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzj(i);
    }

    public Flag zzrr(Parcel parcel) {
        byte[] bArr = null;
        int i = 0;
        int zzcq = zza.zzcq(parcel);
        long j = 0;
        double d = 0.0d;
        int i2 = 0;
        String str = null;
        boolean z = false;
        String str2 = null;
        int i3 = 0;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i3 = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 3:
                    j = zza.zzi(parcel, zzcp);
                    break;
                case 4:
                    z = zza.zzc(parcel, zzcp);
                    break;
                case 5:
                    d = zza.zzn(parcel, zzcp);
                    break;
                case 6:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 7:
                    bArr = zza.zzt(parcel, zzcp);
                    break;
                case 8:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 9:
                    i = zza.zzg(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new Flag(i3, str2, j, z, d, str, bArr, i2, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public Flag[] zzzj(int i) {
        return new Flag[i];
    }
}
