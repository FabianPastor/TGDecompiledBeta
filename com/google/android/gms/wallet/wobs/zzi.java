package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzi implements Creator<zzf> {
    static void zza(zzf com_google_android_gms_wallet_wobs_zzf, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_wobs_zzf.label, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzf.zzbSC, i, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_wobs_zzf.type, false);
        zzc.zza(parcel, 5, com_google_android_gms_wallet_wobs_zzf.zzbQJ, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkF(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpe(i);
    }

    public zzf zzkF(Parcel parcel) {
        zzl com_google_android_gms_wallet_wobs_zzl = null;
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        zzg com_google_android_gms_wallet_wobs_zzg = null;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            String str3;
            zzg com_google_android_gms_wallet_wobs_zzg2;
            String zzq;
            zzl com_google_android_gms_wallet_wobs_zzl2;
            int zzaX = zzb.zzaX(parcel);
            zzl com_google_android_gms_wallet_wobs_zzl3;
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    com_google_android_gms_wallet_wobs_zzl3 = com_google_android_gms_wallet_wobs_zzl;
                    str3 = str;
                    com_google_android_gms_wallet_wobs_zzg2 = com_google_android_gms_wallet_wobs_zzg;
                    zzq = zzb.zzq(parcel, zzaX);
                    com_google_android_gms_wallet_wobs_zzl2 = com_google_android_gms_wallet_wobs_zzl3;
                    break;
                case 3:
                    zzq = str2;
                    String str4 = str;
                    com_google_android_gms_wallet_wobs_zzg2 = (zzg) zzb.zza(parcel, zzaX, zzg.CREATOR);
                    com_google_android_gms_wallet_wobs_zzl2 = com_google_android_gms_wallet_wobs_zzl;
                    str3 = str4;
                    break;
                case 4:
                    com_google_android_gms_wallet_wobs_zzg2 = com_google_android_gms_wallet_wobs_zzg;
                    zzq = str2;
                    com_google_android_gms_wallet_wobs_zzl3 = com_google_android_gms_wallet_wobs_zzl;
                    str3 = zzb.zzq(parcel, zzaX);
                    com_google_android_gms_wallet_wobs_zzl2 = com_google_android_gms_wallet_wobs_zzl3;
                    break;
                case 5:
                    com_google_android_gms_wallet_wobs_zzl2 = (zzl) zzb.zza(parcel, zzaX, zzl.CREATOR);
                    str3 = str;
                    com_google_android_gms_wallet_wobs_zzg2 = com_google_android_gms_wallet_wobs_zzg;
                    zzq = str2;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    com_google_android_gms_wallet_wobs_zzl2 = com_google_android_gms_wallet_wobs_zzl;
                    str3 = str;
                    com_google_android_gms_wallet_wobs_zzg2 = com_google_android_gms_wallet_wobs_zzg;
                    zzq = str2;
                    break;
            }
            str2 = zzq;
            com_google_android_gms_wallet_wobs_zzg = com_google_android_gms_wallet_wobs_zzg2;
            str = str3;
            com_google_android_gms_wallet_wobs_zzl = com_google_android_gms_wallet_wobs_zzl2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzf(str2, com_google_android_gms_wallet_wobs_zzg, str, com_google_android_gms_wallet_wobs_zzl);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzf[] zzpe(int i) {
        return new zzf[i];
    }
}
