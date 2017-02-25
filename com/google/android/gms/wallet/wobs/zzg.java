package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzg extends zza {
    public static final Creator<zzg> CREATOR = new zzh();
    String zzbQn;
    int zzbSD;
    String zzbSE;
    double zzbSF;
    long zzbSG;
    int zzbSH;

    zzg() {
        this.zzbSH = -1;
        this.zzbSD = -1;
        this.zzbSF = -1.0d;
    }

    zzg(int i, String str, double d, String str2, long j, int i2) {
        this.zzbSD = i;
        this.zzbSE = str;
        this.zzbSF = d;
        this.zzbQn = str2;
        this.zzbSG = j;
        this.zzbSH = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }
}
