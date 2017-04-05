package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbak extends zza implements Result {
    public static final Creator<zzbak> CREATOR = new zzbal();
    final int zzaiI;
    private int zzbEn;
    private Intent zzbEo;

    public zzbak() {
        this(0, null);
    }

    zzbak(int i, int i2, Intent intent) {
        this.zzaiI = i;
        this.zzbEn = i2;
        this.zzbEo = intent;
    }

    public zzbak(int i, Intent intent) {
        this(2, i, intent);
    }

    public Status getStatus() {
        return this.zzbEn == 0 ? Status.zzazx : Status.zzazB;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbal.zza(this, parcel, i);
    }

    public int zzPP() {
        return this.zzbEn;
    }

    public Intent zzPQ() {
        return this.zzbEo;
    }
}
