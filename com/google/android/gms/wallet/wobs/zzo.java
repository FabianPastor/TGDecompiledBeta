package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzo implements Creator<zzn> {
    static void zza(zzn com_google_android_gms_wallet_wobs_zzn, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_wobs_zzn.zzbSL, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzn.description, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkI(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzph(i);
    }

    public zzn zzkI(Parcel parcel) {
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
            return new zzn(str2, str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzn[] zzph(int i) {
        return new zzn[i];
    }
}
