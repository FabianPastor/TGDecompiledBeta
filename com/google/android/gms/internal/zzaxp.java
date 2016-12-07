package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzaxp extends zza implements Result {
    public static final Creator<zzaxp> CREATOR = new zzaxq();
    final int mVersionCode;
    private int zzbCl;
    private Intent zzbCm;

    public zzaxp() {
        this(0, null);
    }

    zzaxp(int i, int i2, Intent intent) {
        this.mVersionCode = i;
        this.zzbCl = i2;
        this.zzbCm = intent;
    }

    public zzaxp(int i, Intent intent) {
        this(2, i, intent);
    }

    public Status getStatus() {
        return this.zzbCl == 0 ? Status.zzayh : Status.zzayl;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaxq.zza(this, parcel, i);
    }

    public int zzOk() {
        return this.zzbCl;
    }

    public Intent zzOl() {
        return this.zzbCm;
    }
}
