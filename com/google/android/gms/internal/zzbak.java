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
    private int zzbEq;
    private Intent zzbEr;

    public zzbak() {
        this(0, null);
    }

    zzbak(int i, int i2, Intent intent) {
        this.zzaiI = i;
        this.zzbEq = i2;
        this.zzbEr = intent;
    }

    public zzbak(int i, Intent intent) {
        this(2, i, intent);
    }

    public Status getStatus() {
        return this.zzbEq == 0 ? Status.zzazx : Status.zzazB;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbal.zza(this, parcel, i);
    }

    public int zzPR() {
        return this.zzbEq;
    }

    public Intent zzPS() {
        return this.zzbEr;
    }
}
