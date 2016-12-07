package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzah extends zza {
    public static final Creator<zzah> CREATOR = new zzai();
    final int mVersionCode;
    @Deprecated
    private final Scope[] zzaDy;
    private final int zzaFj;
    private final int zzaFk;

    zzah(int i, int i2, int i3, Scope[] scopeArr) {
        this.mVersionCode = i;
        this.zzaFj = i2;
        this.zzaFk = i3;
        this.zzaDy = scopeArr;
    }

    public zzah(int i, int i2, Scope[] scopeArr) {
        this(1, i, i2, null);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzai.zza(this, parcel, i);
    }

    public int zzxD() {
        return this.zzaFj;
    }

    public int zzxE() {
        return this.zzaFk;
    }

    @Deprecated
    public Scope[] zzxF() {
        return this.zzaDy;
    }
}
