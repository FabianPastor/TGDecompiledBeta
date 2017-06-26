package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class FullWallet extends zza implements ReflectedParcelable {
    public static final Creator<FullWallet> CREATOR = new zzf();
    private String zzbOo;
    private String zzbOp;
    private ProxyCard zzbOq;
    private String zzbOr;
    private zza zzbOs;
    private zza zzbOt;
    private String[] zzbOu;
    private UserAddress zzbOv;
    private UserAddress zzbOw;
    private InstrumentInfo[] zzbOx;
    private PaymentMethodToken zzbOy;

    private FullWallet() {
    }

    FullWallet(String str, String str2, ProxyCard proxyCard, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, String[] strArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr, PaymentMethodToken paymentMethodToken) {
        this.zzbOo = str;
        this.zzbOp = str2;
        this.zzbOq = proxyCard;
        this.zzbOr = str3;
        this.zzbOs = com_google_android_gms_wallet_zza;
        this.zzbOt = com_google_android_gms_wallet_zza2;
        this.zzbOu = strArr;
        this.zzbOv = userAddress;
        this.zzbOw = userAddress2;
        this.zzbOx = instrumentInfoArr;
        this.zzbOy = paymentMethodToken;
    }

    public final UserAddress getBuyerBillingAddress() {
        return this.zzbOv;
    }

    public final UserAddress getBuyerShippingAddress() {
        return this.zzbOw;
    }

    public final String getEmail() {
        return this.zzbOr;
    }

    public final String getGoogleTransactionId() {
        return this.zzbOo;
    }

    public final InstrumentInfo[] getInstrumentInfos() {
        return this.zzbOx;
    }

    public final String getMerchantTransactionId() {
        return this.zzbOp;
    }

    public final String[] getPaymentDescriptions() {
        return this.zzbOu;
    }

    public final PaymentMethodToken getPaymentMethodToken() {
        return this.zzbOy;
    }

    public final ProxyCard getProxyCard() {
        return this.zzbOq;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOo, false);
        zzd.zza(parcel, 3, this.zzbOp, false);
        zzd.zza(parcel, 4, this.zzbOq, i, false);
        zzd.zza(parcel, 5, this.zzbOr, false);
        zzd.zza(parcel, 6, this.zzbOs, i, false);
        zzd.zza(parcel, 7, this.zzbOt, i, false);
        zzd.zza(parcel, 8, this.zzbOu, false);
        zzd.zza(parcel, 9, this.zzbOv, i, false);
        zzd.zza(parcel, 10, this.zzbOw, i, false);
        zzd.zza(parcel, 11, this.zzbOx, i, false);
        zzd.zza(parcel, 12, this.zzbOy, i, false);
        zzd.zzI(parcel, zze);
    }
}
