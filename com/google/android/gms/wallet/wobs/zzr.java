package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import com.google.android.gms.common.internal.safeparcel.zzb;

public final class zzr implements Creator<zzq> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        zzo com_google_android_gms_wallet_wobs_zzo = null;
        int zzd = zzb.zzd(parcel);
        zzo com_google_android_gms_wallet_wobs_zzo2 = null;
        zzm com_google_android_gms_wallet_wobs_zzm = null;
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 2:
                    str2 = zzb.zzq(parcel, readInt);
                    break;
                case 3:
                    str = zzb.zzq(parcel, readInt);
                    break;
                case 4:
                    com_google_android_gms_wallet_wobs_zzm = (zzm) zzb.zza(parcel, readInt, zzm.CREATOR);
                    break;
                case 5:
                    com_google_android_gms_wallet_wobs_zzo2 = (zzo) zzb.zza(parcel, readInt, zzo.CREATOR);
                    break;
                case 6:
                    com_google_android_gms_wallet_wobs_zzo = (zzo) zzb.zza(parcel, readInt, zzo.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, readInt);
                    break;
            }
        }
        zzb.zzF(parcel, zzd);
        return new zzq(str2, str, com_google_android_gms_wallet_wobs_zzm, com_google_android_gms_wallet_wobs_zzo2, com_google_android_gms_wallet_wobs_zzo);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzq[i];
    }
}
