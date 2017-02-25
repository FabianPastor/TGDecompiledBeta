package com.google.android.gms.wallet.firstparty;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzb implements Creator<zza> {
    static void zza(zza com_google_android_gms_wallet_firstparty_zza, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_firstparty_zza.zzbRE, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_firstparty_zza.zzbRF, false);
        zzc.zza(parcel, 4, com_google_android_gms_wallet_firstparty_zza.zzbRG, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkr(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoN(i);
    }

    public zza zzkr(Parcel parcel) {
        zzm com_google_android_gms_wallet_firstparty_zzm = null;
        int zzaY = com.google.android.gms.common.internal.safeparcel.zzb.zzaY(parcel);
        byte[] bArr = null;
        byte[] bArr2 = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = com.google.android.gms.common.internal.safeparcel.zzb.zzaX(parcel);
            switch (com.google.android.gms.common.internal.safeparcel.zzb.zzdc(zzaX)) {
                case 2:
                    bArr2 = com.google.android.gms.common.internal.safeparcel.zzb.zzt(parcel, zzaX);
                    break;
                case 3:
                    bArr = com.google.android.gms.common.internal.safeparcel.zzb.zzt(parcel, zzaX);
                    break;
                case 4:
                    com_google_android_gms_wallet_firstparty_zzm = (zzm) com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, zzaX, zzm.CREATOR);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zza(bArr2, bArr, com_google_android_gms_wallet_firstparty_zzm);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zza[] zzoN(int i) {
        return new zza[i];
    }
}
