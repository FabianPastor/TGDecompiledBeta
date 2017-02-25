package com.google.android.gms.wallet.firstparty;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzm extends zza {
    public static final Creator<zzm> CREATOR = new zzn();
    int zzbRO;
    Bundle zzbRP;
    String zzbRQ;

    public zzm() {
        this.zzbRO = 0;
        this.zzbRP = new Bundle();
        this.zzbRQ = "";
    }

    zzm(int i, Bundle bundle, String str) {
        this.zzbRP = bundle;
        this.zzbRO = i;
        this.zzbRQ = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzn.zza(this, parcel, i);
    }
}
