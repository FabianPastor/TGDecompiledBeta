package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.CountrySpecification;
import com.google.android.gms.internal.zzbfn;
import java.util.ArrayList;

public final class zzz implements Creator<MaskedWalletRequest> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        String str = null;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        Cart cart = null;
        boolean z4 = false;
        boolean z5 = false;
        CountrySpecification[] countrySpecificationArr = null;
        boolean z6 = true;
        boolean z7 = true;
        ArrayList arrayList = null;
        PaymentMethodTokenizationParameters paymentMethodTokenizationParameters = null;
        ArrayList arrayList2 = null;
        String str5 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    z = zzbfn.zzc(parcel, readInt);
                    break;
                case 4:
                    z2 = zzbfn.zzc(parcel, readInt);
                    break;
                case 5:
                    z3 = zzbfn.zzc(parcel, readInt);
                    break;
                case 6:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 7:
                    str3 = zzbfn.zzq(parcel, readInt);
                    break;
                case 8:
                    str4 = zzbfn.zzq(parcel, readInt);
                    break;
                case 9:
                    cart = (Cart) zzbfn.zza(parcel, readInt, Cart.CREATOR);
                    break;
                case 10:
                    z4 = zzbfn.zzc(parcel, readInt);
                    break;
                case 11:
                    z5 = zzbfn.zzc(parcel, readInt);
                    break;
                case 12:
                    countrySpecificationArr = (CountrySpecification[]) zzbfn.zzb(parcel, readInt, CountrySpecification.CREATOR);
                    break;
                case 13:
                    z6 = zzbfn.zzc(parcel, readInt);
                    break;
                case 14:
                    z7 = zzbfn.zzc(parcel, readInt);
                    break;
                case 15:
                    arrayList = zzbfn.zzc(parcel, readInt, CountrySpecification.CREATOR);
                    break;
                case 16:
                    paymentMethodTokenizationParameters = (PaymentMethodTokenizationParameters) zzbfn.zza(parcel, readInt, PaymentMethodTokenizationParameters.CREATOR);
                    break;
                case 17:
                    arrayList2 = zzbfn.zzab(parcel, readInt);
                    break;
                case 18:
                    str5 = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new MaskedWalletRequest(str, z, z2, z3, str2, str3, str4, cart, z4, z5, countrySpecificationArr, z6, z7, arrayList, paymentMethodTokenizationParameters, arrayList2, str5);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new MaskedWalletRequest[i];
    }
}
