package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzd extends zza {
    public static final Creator<zzd> CREATOR = new zze();
    final int mVersionCode;
    Integer zzaDA;
    final IBinder zzaDx;
    final Scope[] zzaDy;
    Integer zzaDz;

    zzd(int i, IBinder iBinder, Scope[] scopeArr, Integer num, Integer num2) {
        this.mVersionCode = i;
        this.zzaDx = iBinder;
        this.zzaDy = scopeArr;
        this.zzaDz = num;
        this.zzaDA = num2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze.zza(this, parcel, i);
    }
}
