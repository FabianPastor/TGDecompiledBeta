package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfn;

public final class zzac implements Creator<PaymentData> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzbfn.zzd(parcel);
        PaymentMethodToken paymentMethodToken = null;
        UserAddress userAddress = null;
        CardInfo cardInfo = null;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 2:
                    cardInfo = (CardInfo) zzbfn.zza(parcel, readInt, CardInfo.CREATOR);
                    break;
                case 3:
                    userAddress = (UserAddress) zzbfn.zza(parcel, readInt, UserAddress.CREATOR);
                    break;
                case 4:
                    paymentMethodToken = (PaymentMethodToken) zzbfn.zza(parcel, readInt, PaymentMethodToken.CREATOR);
                    break;
                case 5:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new PaymentData(str2, cardInfo, userAddress, paymentMethodToken, str);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new PaymentData[i];
    }
}
