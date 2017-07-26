package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class FullWallet extends zza implements ReflectedParcelable {
    public static final Creator<FullWallet> CREATOR = new zzf();
    private PaymentMethodToken zzbOA;
    private String zzbOq;
    private String zzbOr;
    private ProxyCard zzbOs;
    private String zzbOt;
    private zza zzbOu;
    private zza zzbOv;
    private String[] zzbOw;
    private UserAddress zzbOx;
    private UserAddress zzbOy;
    private InstrumentInfo[] zzbOz;

    private FullWallet() {
    }

    FullWallet(String str, String str2, ProxyCard proxyCard, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, String[] strArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr, PaymentMethodToken paymentMethodToken) {
        this.zzbOq = str;
        this.zzbOr = str2;
        this.zzbOs = proxyCard;
        this.zzbOt = str3;
        this.zzbOu = com_google_android_gms_wallet_zza;
        this.zzbOv = com_google_android_gms_wallet_zza2;
        this.zzbOw = strArr;
        this.zzbOx = userAddress;
        this.zzbOy = userAddress2;
        this.zzbOz = instrumentInfoArr;
        this.zzbOA = paymentMethodToken;
    }

    public final UserAddress getBuyerBillingAddress() {
        return this.zzbOx;
    }

    public final UserAddress getBuyerShippingAddress() {
        return this.zzbOy;
    }

    public final String getEmail() {
        return this.zzbOt;
    }

    public final String getGoogleTransactionId() {
        return this.zzbOq;
    }

    public final InstrumentInfo[] getInstrumentInfos() {
        return this.zzbOz;
    }

    public final String getMerchantTransactionId() {
        return this.zzbOr;
    }

    public final String[] getPaymentDescriptions() {
        return this.zzbOw;
    }

    public final PaymentMethodToken getPaymentMethodToken() {
        return this.zzbOA;
    }

    public final ProxyCard getProxyCard() {
        return this.zzbOs;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOq, false);
        zzd.zza(parcel, 3, this.zzbOr, false);
        zzd.zza(parcel, 4, this.zzbOs, i, false);
        zzd.zza(parcel, 5, this.zzbOt, false);
        zzd.zza(parcel, 6, this.zzbOu, i, false);
        zzd.zza(parcel, 7, this.zzbOv, i, false);
        zzd.zza(parcel, 8, this.zzbOw, false);
        zzd.zza(parcel, 9, this.zzbOx, i, false);
        zzd.zza(parcel, 10, this.zzbOy, i, false);
        zzd.zza(parcel, 11, this.zzbOz, i, false);
        zzd.zza(parcel, 12, this.zzbOA, i, false);
        zzd.zzI(parcel, zze);
    }
}
