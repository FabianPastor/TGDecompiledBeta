package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class FullWallet extends zzbfm implements ReflectedParcelable {
    public static final Creator<FullWallet> CREATOR = new zzk();
    private String zzlaa;
    private String zzlab;
    private ProxyCard zzlac;
    private String zzlad;
    private zza zzlae;
    private zza zzlaf;
    private String[] zzlag;
    private UserAddress zzlah;
    private UserAddress zzlai;
    private InstrumentInfo[] zzlaj;
    private PaymentMethodToken zzlak;

    private FullWallet() {
    }

    FullWallet(String str, String str2, ProxyCard proxyCard, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, String[] strArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr, PaymentMethodToken paymentMethodToken) {
        this.zzlaa = str;
        this.zzlab = str2;
        this.zzlac = proxyCard;
        this.zzlad = str3;
        this.zzlae = com_google_android_gms_wallet_zza;
        this.zzlaf = com_google_android_gms_wallet_zza2;
        this.zzlag = strArr;
        this.zzlah = userAddress;
        this.zzlai = userAddress2;
        this.zzlaj = instrumentInfoArr;
        this.zzlak = paymentMethodToken;
    }

    public final String getGoogleTransactionId() {
        return this.zzlaa;
    }

    public final String[] getPaymentDescriptions() {
        return this.zzlag;
    }

    public final PaymentMethodToken getPaymentMethodToken() {
        return this.zzlak;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlaa, false);
        zzbfp.zza(parcel, 3, this.zzlab, false);
        zzbfp.zza(parcel, 4, this.zzlac, i, false);
        zzbfp.zza(parcel, 5, this.zzlad, false);
        zzbfp.zza(parcel, 6, this.zzlae, i, false);
        zzbfp.zza(parcel, 7, this.zzlaf, i, false);
        zzbfp.zza(parcel, 8, this.zzlag, false);
        zzbfp.zza(parcel, 9, this.zzlah, i, false);
        zzbfp.zza(parcel, 10, this.zzlai, i, false);
        zzbfp.zza(parcel, 11, this.zzlaj, i, false);
        zzbfp.zza(parcel, 12, this.zzlak, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
