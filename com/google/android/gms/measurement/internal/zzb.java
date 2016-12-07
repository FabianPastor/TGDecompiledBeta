package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzb implements Creator<AppMetadata> {
    static void zza(AppMetadata appMetadata, Parcel parcel, int i) {
        int zzcr = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, appMetadata.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 2, appMetadata.packageName, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, appMetadata.anQ, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, appMetadata.afY, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, appMetadata.anR, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, appMetadata.anS);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, appMetadata.anT);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, appMetadata.anU, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 9, appMetadata.anV);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 10, appMetadata.anW);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 11, appMetadata.anX);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 12, appMetadata.anY, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzpe(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzwk(i);
    }

    public AppMetadata zzpe(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        long j = 0;
        long j2 = 0;
        String str5 = null;
        boolean z = false;
        boolean z2 = false;
        long j3 = 0;
        String str6 = null;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    str = zza.zzq(parcel, zzcp);
                    break;
                case 3:
                    str2 = zza.zzq(parcel, zzcp);
                    break;
                case 4:
                    str3 = zza.zzq(parcel, zzcp);
                    break;
                case 5:
                    str4 = zza.zzq(parcel, zzcp);
                    break;
                case 6:
                    j = zza.zzi(parcel, zzcp);
                    break;
                case 7:
                    j2 = zza.zzi(parcel, zzcp);
                    break;
                case 8:
                    str5 = zza.zzq(parcel, zzcp);
                    break;
                case 9:
                    z = zza.zzc(parcel, zzcp);
                    break;
                case 10:
                    z2 = zza.zzc(parcel, zzcp);
                    break;
                case 11:
                    j3 = zza.zzi(parcel, zzcp);
                    break;
                case 12:
                    str6 = zza.zzq(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new AppMetadata(i, str, str2, str3, str4, j, j2, str5, z, z2, j3, str6);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }

    public AppMetadata[] zzwk(int i) {
        return new AppMetadata[i];
    }
}
