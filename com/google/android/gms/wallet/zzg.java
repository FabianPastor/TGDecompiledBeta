package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.identity.intents.model.UserAddress;

public class zzg implements Creator<FullWallet> {
    static void zza(FullWallet fullWallet, Parcel parcel, int i) {
        int zzaZ = zzc.zzaZ(parcel);
        zzc.zza(parcel, 2, fullWallet.zzbPW, false);
        zzc.zza(parcel, 3, fullWallet.zzbPX, false);
        zzc.zza(parcel, 4, fullWallet.zzbPY, i, false);
        zzc.zza(parcel, 5, fullWallet.zzbPZ, false);
        zzc.zza(parcel, 6, fullWallet.zzbQa, i, false);
        zzc.zza(parcel, 7, fullWallet.zzbQb, i, false);
        zzc.zza(parcel, 8, fullWallet.zzbQc, false);
        zzc.zza(parcel, 9, fullWallet.zzbQd, i, false);
        zzc.zza(parcel, 10, fullWallet.zzbQe, i, false);
        zzc.zza(parcel, 11, fullWallet.zzbQf, i, false);
        zzc.zza(parcel, 12, fullWallet.zzbQg, i, false);
        zzc.zzJ(parcel, zzaZ);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zzkb(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzox(i);
    }

    public FullWallet zzkb(Parcel parcel) {
        PaymentMethodToken paymentMethodToken = null;
        int zzaY = zzb.zzaY(parcel);
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
        while (parcel.dataPosition() < zzaY) {
            int zzaX = zzb.zzaX(parcel);
            switch (zzb.zzdc(zzaX)) {
                case 2:
                    str3 = zzb.zzq(parcel, zzaX);
                    break;
                case 3:
                    str2 = zzb.zzq(parcel, zzaX);
                    break;
                case 4:
                    proxyCard = (ProxyCard) zzb.zza(parcel, zzaX, ProxyCard.CREATOR);
                    break;
                case 5:
                    str = zzb.zzq(parcel, zzaX);
                    break;
                case 6:
                    com_google_android_gms_wallet_zza2 = (zza) zzb.zza(parcel, zzaX, zza.CREATOR);
                    break;
                case 7:
                    com_google_android_gms_wallet_zza = (zza) zzb.zza(parcel, zzaX, zza.CREATOR);
                    break;
                case 8:
                    strArr = zzb.zzC(parcel, zzaX);
                    break;
                case 9:
                    userAddress2 = (UserAddress) zzb.zza(parcel, zzaX, UserAddress.CREATOR);
                    break;
                case 10:
                    userAddress = (UserAddress) zzb.zza(parcel, zzaX, UserAddress.CREATOR);
                    break;
                case 11:
                    instrumentInfoArr = (InstrumentInfo[]) zzb.zzb(parcel, zzaX, InstrumentInfo.CREATOR);
                    break;
                case 12:
                    paymentMethodToken = (PaymentMethodToken) zzb.zza(parcel, zzaX, PaymentMethodToken.CREATOR);
                    break;
                default:
                    zzb.zzb(parcel, zzaX);
                    break;
            }
        }
        if (parcel.dataPosition() == zzaY) {
            return new FullWallet(str3, str2, proxyCard, str, com_google_android_gms_wallet_zza2, com_google_android_gms_wallet_zza, strArr, userAddress2, userAddress, instrumentInfoArr, paymentMethodToken);
        }
        throw new zza("Overread allowed size end=" + zzaY, parcel);
    }

    public FullWallet[] zzox(int i) {
        return new FullWallet[i];
    }
}
