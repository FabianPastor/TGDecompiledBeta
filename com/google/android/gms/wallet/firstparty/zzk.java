package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzk implements Creator<zzj> {
    static void zza(zzj com_google_android_gms_wallet_firstparty_zzj, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_firstparty_zzj.zzbRM, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkv(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoR(i);
    }

    public zzj zzkv(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        byte[] bArr = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    bArr = zzb.zzt(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzj(bArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzj[] zzoR(int i) {
        return new zzj[i];
    }
}
