package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class GiftCardWalletObject extends zza {
    public static final Creator<GiftCardWalletObject> CREATOR = new zzi();
    String pin;
    CommonWalletObject zzbQj = CommonWalletObject.zzUb().zzUc();
    String zzbQk;
    String zzbQl;
    long zzbQm;
    String zzbQn;
    long zzbQo;
    String zzbQp;

    GiftCardWalletObject() {
    }

    GiftCardWalletObject(CommonWalletObject commonWalletObject, String str, String str2, String str3, long j, String str4, long j2, String str5) {
        this.zzbQj = commonWalletObject;
        this.zzbQk = str;
        this.pin = str2;
        this.zzbQm = j;
        this.zzbQn = str4;
        this.zzbQo = j2;
        this.zzbQp = str5;
        this.zzbQl = str3;
    }

    public String getId() {
        return this.zzbQj.getId();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzi.zza(this, parcel, i);
    }
}
