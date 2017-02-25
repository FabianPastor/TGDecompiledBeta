package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzd extends zza {
    public static final Creator<zzd> CREATOR = new zze();
    final IBinder zzaEW;
    final Scope[] zzaEX;
    Integer zzaEY;
    Integer zzaEZ;
    final int zzaiI;

    zzd(int i, IBinder iBinder, Scope[] scopeArr, Integer num, Integer num2) {
        this.zzaiI = i;
        this.zzaEW = iBinder;
        this.zzaEX = scopeArr;
        this.zzaEY = num;
        this.zzaEZ = num2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze.zza(this, parcel, i);
    }
}
