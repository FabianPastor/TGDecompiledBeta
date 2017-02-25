package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzi implements Creator<zzh> {
    static void zza(zzh com_google_android_gms_wallet_firstparty_zzh, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_firstparty_zzh.zzbRK, i, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_firstparty_zzh.zzbRL);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzku(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoQ(i);
    }

    public zzh zzku(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        zzm com_google_android_gms_wallet_firstparty_zzm = null;
        boolean z = false;
        while (parcel.dataPosition() < zzaY) {
            zzm com_google_android_gms_wallet_firstparty_zzm2;
            boolean z2;
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    boolean z3 = z;
                    com_google_android_gms_wallet_firstparty_zzm2 = (zzm) zzb.zza(parcel, zzaX, zzm.CREATOR);
                    z2 = z3;
                    break;
                case 3:
                    z2 = zzb.zzc(parcel, zzaX);
                    com_google_android_gms_wallet_firstparty_zzm2 = com_google_android_gms_wallet_firstparty_zzm;
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    z2 = z;
                    com_google_android_gms_wallet_firstparty_zzm2 = com_google_android_gms_wallet_firstparty_zzm;
                    break;
            }
            com_google_android_gms_wallet_firstparty_zzm = com_google_android_gms_wallet_firstparty_zzm2;
            z = z2;
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzh(com_google_android_gms_wallet_firstparty_zzm, z);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzh[] zzoQ(int i) {
        return new zzh[i];
    }
}
