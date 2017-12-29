package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfn;

public final class zzk implements Creator<FullWallet> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        PaymentMethodToken paymentMethodToken = null;
        int zzd = zzbfn.zzd(parcel);
        InstrumentInfo[] instrumentInfoArr = null;
        UserAddress userAddress = null;
        UserAddress userAddress2 = null;
        String[] strArr = null;
        zza com_google_android_gms_wallet_zza = null;
        zza com_google_android_gms_wallet_zza2 = null;
        String str = null;
        ProxyCard proxyCard = null;
        String str2 = null;
        String str3 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    proxyCard = (ProxyCard) zzbfn.zza(parcel, readInt, ProxyCard.CREATOR);
                    break;
                case 5:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    com_google_android_gms_wallet_zza2 = (zza) zzbfn.zza(parcel, readInt, zza.CREATOR);
                    break;
                case 7:
                    com_google_android_gms_wallet_zza = (zza) zzbfn.zza(parcel, readInt, zza.CREATOR);
                    break;
                case 8:
                    strArr = zzbfn.zzaa(parcel, readInt);
                    break;
                case 9:
                    userAddress2 = (UserAddress) zzbfn.zza(parcel, readInt, UserAddress.CREATOR);
                    break;
                case 10:
                    userAddress = (UserAddress) zzbfn.zza(parcel, readInt, UserAddress.CREATOR);
                    break;
                case 11:
                    instrumentInfoArr = (InstrumentInfo[]) zzbfn.zzb(parcel, readInt, InstrumentInfo.CREATOR);
                    break;
                case 12:
                    paymentMethodToken = (PaymentMethodToken) zzbfn.zza(parcel, readInt, PaymentMethodToken.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new FullWallet(str3, str2, proxyCard, str, com_google_android_gms_wallet_zza2, com_google_android_gms_wallet_zza, strArr, userAddress2, userAddress, instrumentInfoArr, paymentMethodToken);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new FullWallet[i];
    }
}
