package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zze implements Creator<zzd> {
    static void zza(zzd com_google_android_gms_wallet_firstparty_zzd, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_firstparty_zzd.zzbRH, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_firstparty_zzd.zzbRI, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzks(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoO(i);
    }

    public zzd zzks(Parcel parcel) {
        byte[] bArr = null;
        int zzaY = zzb.zzaY(parcel);
        byte[] bArr2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    bArr2 = zzb.zzt(parcel, zzaX);
                    break;
                case 3:
                    bArr = zzb.zzt(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzd(bArr2, bArr);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzd[] zzoO(int i) {
        return new zzd[i];
    }
}
