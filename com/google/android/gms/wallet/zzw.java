package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzw extends zza {
    public static final Creator<zzw> CREATOR = new zzx();
    Cart zzbRB;
    String zzbRC;
    String zzbRD;

    private zzw() {
    }

    zzw(Cart cart, String str, String str2) {
        this.zzbRB = cart;
        this.zzbRC = str;
        this.zzbRD = str2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzx.zza(this, parcel, i);
    }
}
