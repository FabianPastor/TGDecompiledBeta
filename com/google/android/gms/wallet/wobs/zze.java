package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import java.util.ArrayList;

public class zze implements Creator<zzd> {
    static void zza(zzd com_google_android_gms_wallet_wobs_zzd, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, com_google_android_gms_wallet_wobs_zzd.zzbSz, false);
        zzc.zza(parcel, 3, com_google_android_gms_wallet_wobs_zzd.zzbSA, false);
        zzc.zzc(parcel, 4, com_google_android_gms_wallet_wobs_zzd.zzbSB, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkD(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzpc(i);
    }

    public zzd zzkD(Parcel parcel) {
        String str = null;
        int zzaY = zzb.zzaY(parcel);
        ArrayList zzyY = com.google.android.gms.common.util.zzb.zzyY();
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
                case 4:
                    zzyY = zzb.zzc(parcel, zzaX, zzb.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new zzd(str2, str, zzyY);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public zzd[] zzpc(int i) {
        return new zzd[i];
    }
}
