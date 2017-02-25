package com.google.android.gms.wallet.firstparty;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzn implements Creator<zzm> {
    static void zza(zzm com_google_android_gms_wallet_firstparty_zzm, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zzc(parcel, 2, com_google_android_gms_wallet_firstparty_zzm.zzbRO);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_firstparty_zzm.zzbRP, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_firstparty_zzm.zzbRQ, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkx(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoT(i);
    }

    public zzm zzkx(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        int i = 0;
        Bundle bundle = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    i = zzb.zzg(parcel, zzaX);
                    break;
                case 3:
                    bundle = zzb.zzs(parcel, zzaX);
                    break;
                case 4:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzm(i, bundle, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzm[] zzoT(int i) {
        return new zzm[i];
    }
}
