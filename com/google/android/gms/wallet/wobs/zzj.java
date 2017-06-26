package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzj implements Creator<zzg> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzb.zzd(parcel);
        zzm com_google_android_gms_wallet_wobs_zzm = null;
        String str = null;
        zzh com_google_android_gms_wallet_wobs_zzh = null;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 3:
                    com_google_android_gms_wallet_wobs_zzh = (zzh) zzb.zza(parcel, readInt, zzh.CREATOR);
                    break;
                case 4:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 5:
                    com_google_android_gms_wallet_wobs_zzm = (zzm) zzb.zza(parcel, readInt, zzm.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzg(str2, com_google_android_gms_wallet_wobs_zzh, str, com_google_android_gms_wallet_wobs_zzm);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzg[i];
    }
}
