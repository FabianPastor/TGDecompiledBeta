package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class FullWallet extends zza implements ReflectedParcelable {
    public static final Creator<FullWallet> CREATOR = new zzg();
    String zzbPW;
    String zzbPX;
    ProxyCard zzbPY;
    String zzbPZ;
    zza zzbQa;
    zza zzbQb;
    String[] zzbQc;
    UserAddress zzbQd;
    UserAddress zzbQe;
    InstrumentInfo[] zzbQf;
    PaymentMethodToken zzbQg;

    private FullWallet() {
    }

    FullWallet(String str, String str2, ProxyCard proxyCard, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, String[] strArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr, PaymentMethodToken paymentMethodToken) {
        this.zzbPW = str;
        this.zzbPX = str2;
        this.zzbPY = proxyCard;
        this.zzbPZ = str3;
        this.zzbQa = com_google_android_gms_wallet_zza;
        this.zzbQb = com_google_android_gms_wallet_zza2;
        this.zzbQc = strArr;
        this.zzbQd = userAddress;
        this.zzbQe = userAddress2;
        this.zzbQf = instrumentInfoArr;
        this.zzbQg = paymentMethodToken;
    }

    public UserAddress getBuyerBillingAddress() {
        return this.zzbQd;
    }

    public UserAddress getBuyerShippingAddress() {
        return this.zzbQe;
    }

    public String getEmail() {
        return this.zzbPZ;
    }

    public String getGoogleTransactionId() {
        return this.zzbPW;
    }

    public InstrumentInfo[] getInstrumentInfos() {
        return this.zzbQf;
    }

    public String getMerchantTransactionId() {
        return this.zzbPX;
    }

    public String[] getPaymentDescriptions() {
        return this.zzbQc;
    }

    public PaymentMethodToken getPaymentMethodToken() {
        return this.zzbQg;
    }

    public ProxyCard getProxyCard() {
        return this.zzbPY;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }
}
