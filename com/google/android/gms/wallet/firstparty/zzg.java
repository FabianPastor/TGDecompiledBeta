package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzg implements Creator<zzf> {
    static void zza(zzf com_google_android_gms_wallet_firstparty_zzf, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_firstparty_zzf.zzbRJ, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkt(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoP(i);
    }

    public zzf zzkt(Parcel parcel) {
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
            return new zzf(bArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzf[] zzoP(int i) {
        return new zzf[i];
    }
}
