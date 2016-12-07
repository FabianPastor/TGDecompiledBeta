package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zza implements Creator<Configuration> {
    static void zza(Configuration configuration, Parcel parcel, int i) {
        int zzcs = zzb.zzcs(parcel);
        zzb.zzc(parcel, 1, configuration.mVersionCode);
        zzb.zzc(parcel, 2, configuration.aAs);
        zzb.zza(parcel, 3, configuration.aAt, i, false);
        zzb.zza(parcel, 4, configuration.aAu, false);
        zzb.zzaj(parcel, zzcs);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzrf(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzyx(i);
    }

    public Configuration zzrf(Parcel parcel) {
        String[] strArr = null;
        int i = 0;
        int zzcr = com.google.android.gms.common.internal.safeparcel.zza.zzcr(parcel);
        Flag[] flagArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcr) {
            Flag[] flagArr2;
            int i3;
            String[] strArr2;
            int zzcq = com.google.android.gms.common.internal.safeparcel.zza.zzcq(parcel);
            String[] strArr3;
            switch (com.google.android.gms.common.internal.safeparcel.zza.zzgu(zzcq)) {
                case 1:
                    strArr3 = strArr;
                    flagArr2 = flagArr;
                    i3 = i;
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcq);
                    strArr2 = strArr3;
                    break;
                case 2:
                    i = i2;
                    Flag[] flagArr3 = flagArr;
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcq);
                    strArr2 = strArr;
                    flagArr2 = flagArr3;
                    break;
                case 3:
                    i3 = i;
                    i = i2;
                    strArr3 = strArr;
                    flagArr2 = (Flag[]) com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcq, Flag.CREATOR);
                    strArr2 = strArr3;
                    break;
                case 4:
                    strArr2 = com.google.android.gms.common.internal.safeparcel.zza.zzac(parcel, zzcq);
                    flagArr2 = flagArr;
                    i3 = i;
                    i = i2;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcq);
                    strArr2 = strArr;
                    flagArr2 = flagArr;
                    i3 = i;
                    i = i2;
                    break;
            }
            i2 = i;
            i = i3;
            flagArr = flagArr2;
            strArr = strArr2;
        }
        if (parcel.dataPosition() == zzcr) {
            return new Configuration(i2, i, flagArr, strArr);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zza.zza("Overread allowed size end=" + zzcr, parcel);
    }

    public Configuration[] zzyx(int i) {
        return new Configuration[i];
    }
}
