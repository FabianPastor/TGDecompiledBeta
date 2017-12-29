package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class MaskedWallet extends zzbfm implements ReflectedParcelable {
    public static final Creator<MaskedWallet> CREATOR = new zzx();
    String zzlaa;
    String zzlab;
    String zzlad;
    private zza zzlae;
    private zza zzlaf;
    String[] zzlag;
    UserAddress zzlah;
    UserAddress zzlai;
    InstrumentInfo[] zzlaj;
    private LoyaltyWalletObject[] zzlcc;
    private OfferWalletObject[] zzlcd;

    private MaskedWallet() {
    }

    MaskedWallet(String str, String str2, String[] strArr, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, LoyaltyWalletObject[] loyaltyWalletObjectArr, OfferWalletObject[] offerWalletObjectArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr) {
        this.zzlaa = str;
        this.zzlab = str2;
        this.zzlag = strArr;
        this.zzlad = str3;
        this.zzlae = com_google_android_gms_wallet_zza;
        this.zzlaf = com_google_android_gms_wallet_zza2;
        this.zzlcc = loyaltyWalletObjectArr;
        this.zzlcd = offerWalletObjectArr;
        this.zzlah = userAddress;
        this.zzlai = userAddress2;
        this.zzlaj = instrumentInfoArr;
    }

    public final String getGoogleTransactionId() {
        return this.zzlaa;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlaa, false);
        zzbfp.zza(parcel, 3, this.zzlab, false);
        zzbfp.zza(parcel, 4, this.zzlag, false);
        zzbfp.zza(parcel, 5, this.zzlad, false);
        zzbfp.zza(parcel, 6, this.zzlae, i, false);
        zzbfp.zza(parcel, 7, this.zzlaf, i, false);
        zzbfp.zza(parcel, 8, this.zzlcc, i, false);
        zzbfp.zza(parcel, 9, this.zzlcd, i, false);
        zzbfp.zza(parcel, 10, this.zzlah, i, false);
        zzbfp.zza(parcel, 11, this.zzlai, i, false);
        zzbfp.zza(parcel, 12, this.zzlaj, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
