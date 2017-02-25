package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;

public class zzv implements Creator<zzu> {
    static void zza(zzu com_google_android_gms_wallet_zzu, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_zzu.zzbRA, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkp(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzoL(i);
    }

    public zzu zzkp(Parcel parcel) {
        int zzaY = zzb.zzaY(parcel);
        String str = null;
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzu(str);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzu[] zzoL(int i) {
        return new zzu[i];
    }
}
