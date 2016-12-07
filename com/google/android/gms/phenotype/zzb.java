package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzb implements Creator<Flag> {
    static void zza(Flag flag, Parcel parcel, int i) {
        int zzcs = com.google.android.gms.common.internal.safeparcel.zzb.zzcs(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, flag.mVersionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, flag.name, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, flag.aAw);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, flag.ahI);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, flag.ahK);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, flag.Fe, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, flag.aAx, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 8, flag.aAy);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 9, flag.aAz);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzrg(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzyy(i);
    }

    public Flag zzrg(Parcel parcel) {
        byte[] bArr = null;
        int i = 0;
        int zzcr = zza.zzcr(parcel);
        long j = 0;
        double d = 0.0d;
        int i2 = 0;
        String str = null;
        boolean z = false;
        String str2 = null;
        int i3 = 0;
        while (parcel.dataPosition() < zzcr) {
            int zzcq = zza.zzcq(parcel);
            switch (zza.zzgu(zzcq)) {
                case 1:
                    i3 = zza.zzg(parcel, zzcq);
                    break;
                case 2:
                    str2 = zza.zzq(parcel, zzcq);
                    break;
                case 3:
                    j = zza.zzi(parcel, zzcq);
                    break;
                case 4:
                    z = zza.zzc(parcel, zzcq);
                    break;
                case 5:
                    d = zza.zzn(parcel, zzcq);
                    break;
                case 6:
                    str = zza.zzq(parcel, zzcq);
                    break;
                case 7:
                    bArr = zza.zzt(parcel, zzcq);
                    break;
                case 8:
                    i2 = zza.zzg(parcel, zzcq);
                    break;
                case 9:
                    i = zza.zzg(parcel, zzcq);
                    break;
                default:
                    zza.zzb(parcel, zzcq);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcr) {
            return new Flag(i3, str2, j, z, d, str, bArr, i2, i);
        }
        throw new zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public Flag[] zzyy(int i) {
        return new Flag[i];
    }
}
