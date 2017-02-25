package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

@Deprecated
public final class ProxyCard extends zza {
    public static final Creator<ProxyCard> CREATOR = new zzt();
    String zzbRq;
    String zzbRr;
    int zzbRs;
    int zzbRt;

    public ProxyCard(String str, String str2, int i, int i2) {
        this.zzbRq = str;
        this.zzbRr = str2;
        this.zzbRs = i;
        this.zzbRt = i2;
    }

    public String getCvn() {
        return this.zzbRr;
    }

    public int getExpirationMonth() {
        return this.zzbRs;
    }

    public int getExpirationYear() {
        return this.zzbRt;
    }

    public String getPan() {
        return this.zzbRq;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzt.zza(this, parcel, i);
    }
}
