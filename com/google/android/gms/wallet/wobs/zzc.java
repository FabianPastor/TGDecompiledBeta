package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;

public class zzc implements Creator<zzb> {
    static void zza(zzb com_google_android_gms_wallet_wobs_zzb, Parcel parcel, int i) {
        int zzaZ = com.google.android.gms.common.internal.safeparcel.zzc.zzaZ(parcel);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 2, com_google_android_gms_wallet_wobs_zzb.label, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzb.value, false);
        com.google.android.gms.common.internal.safeparcel.zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkC(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpb(i);
    }

    public zzb zzkC(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
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
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzb(str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzb[] zzpb(int i) {
        return new zzb[i];
    }
}
