package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzm implements Creator<zzl> {
    static void zza(zzl com_google_android_gms_wallet_wobs_zzl, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_wobs_zzl.zzbSJ);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzl.zzbSK);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkH(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpg(i);
    }

    public zzl zzkH(Parcel parcel) {
        long j = 0;
        int zzaY = zzb.zzaY(parcel);
        long j2 = 0;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    j2 = zzb.zzi(parcel, zzaX);
                    break;
                case 3:
                    j = zzb.zzi(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzl(j2, j);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzl[] zzpg(int i) {
        return new zzl[i];
    }
}
