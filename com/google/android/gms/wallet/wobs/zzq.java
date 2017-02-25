package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzq implements Creator<zzp> {
    static void zza(zzp com_google_android_gms_wallet_wobs_zzp, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_wobs_zzp.zzbSI, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzp.body, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_wobs_zzp.zzbSM, i, false);
        zzc.zza(parcel, 5, com_google_android_gms_wallet_wobs_zzp.zzbSN, i, false);
        zzc.zza(parcel, 6, com_google_android_gms_wallet_wobs_zzp.zzbSO, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkJ(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpi(i);
    }

    public zzp zzkJ(Parcel parcel) {
        zzn com_google_android_gms_wallet_wobs_zzn = null;
        int zzaY = zzb.zzaY(parcel);
        zzn com_google_android_gms_wallet_wobs_zzn2 = null;
        zzl com_google_android_gms_wallet_wobs_zzl = null;
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    com_google_android_gms_wallet_wobs_zzl = (zzl) zzb.zza(parcel, zzaX, zzl.CREATOR);
                    break;
                case 5:
                    com_google_android_gms_wallet_wobs_zzn2 = (zzn) zzb.zza(parcel, zzaX, zzn.CREATOR);
                    break;
                case 6:
                    com_google_android_gms_wallet_wobs_zzn = (zzn) zzb.zza(parcel, zzaX, zzn.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzp(str2, str, com_google_android_gms_wallet_wobs_zzl, com_google_android_gms_wallet_wobs_zzn2, com_google_android_gms_wallet_wobs_zzn);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzp[] zzpi(int i) {
        return new zzp[i];
    }
}
