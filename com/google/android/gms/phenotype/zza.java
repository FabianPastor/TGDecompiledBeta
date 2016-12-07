package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;

public class zza implements Creator<Configuration> {
    static void zza(Configuration configuration, Parcel parcel, int i) {
        int zzcr = zzb.zzcr(parcel);
        zzb.zzc(parcel, 1, configuration.mVersionCode);
        zzb.zzc(parcel, 2, configuration.axl);
        zzb.zza(parcel, 3, configuration.axm, i, false);
        zzb.zza(parcel, 4, configuration.axn, false);
        zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzrq(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzzi(i);
    }

    public Configuration zzrq(Parcel parcel) {
        String[] strArr = null;
        int i = 0;
        int zzcq = com.google.android.gms.common.internal.safeparcel.zza.zzcq(parcel);
        Flag[] flagArr = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzcq) {
            Flag[] flagArr2;
            int i3;
            String[] strArr2;
            int zzcp = com.google.android.gms.common.internal.safeparcel.zza.zzcp(parcel);
            String[] strArr3;
            switch (com.google.android.gms.common.internal.safeparcel.zza.zzgv(zzcp)) {
                case 1:
                    strArr3 = strArr;
                    flagArr2 = flagArr;
                    i3 = i;
                    i = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    strArr2 = strArr3;
                    break;
                case 2:
                    i = i2;
                    Flag[] flagArr3 = flagArr;
                    i3 = com.google.android.gms.common.internal.safeparcel.zza.zzg(parcel, zzcp);
                    strArr2 = strArr;
                    flagArr2 = flagArr3;
                    break;
                case 3:
                    i3 = i;
                    i = i2;
                    strArr3 = strArr;
                    flagArr2 = (Flag[]) com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcp, Flag.CREATOR);
                    strArr2 = strArr3;
                    break;
                case 4:
                    strArr2 = com.google.android.gms.common.internal.safeparcel.zza.zzac(parcel, zzcp);
                    flagArr2 = flagArr;
                    i3 = i;
                    i = i2;
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zza.zzb(parcel, zzcp);
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
        if (parcel.dataPosition() == zzcq) {
            return new Configuration(i2, i, flagArr, strArr);
        }
        throw new com.google.android.gms.common.internal.safeparcel.zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public Configuration[] zzzi(int i) {
        return new Configuration[i];
    }
}
