package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzh implements Creator<zzg> {
    static void zza(zzg com_google_android_gms_wallet_wobs_zzg, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, com_google_android_gms_wallet_wobs_zzg.zzbSD);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzg.zzbSE, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_wobs_zzg.zzbSF);
        zzc.zza(parcel, 5, com_google_android_gms_wallet_wobs_zzg.zzbQn, false);
        zzc.zza(parcel, 6, com_google_android_gms_wallet_wobs_zzg.zzbSG);
        zzc.zzc(parcel, 7, com_google_android_gms_wallet_wobs_zzg.zzbSH);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkE(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpd(i);
    }

    public zzg zzkE(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        double d = 0.0d;
        long j = 0;
        int i2 = -1;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    d = zzb.zzn(parcel, zzaX);
                    break;
                case 5:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                case 7:
                    i2 = zzb.zzg(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzg(i, str2, d, str, j, i2);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzg[] zzpd(int i) {
        return new zzg[i];
    }
}
