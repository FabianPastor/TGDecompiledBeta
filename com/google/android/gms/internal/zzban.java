package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.List;

public class zzban extends zza {
    public static final Creator<zzban> CREATOR = new zzbao();
    final int zzaiI;
    final boolean zzbEp;
    final List<Scope> zzbEq;

    zzban(int i, boolean z, List<Scope> list) {
        this.zzaiI = i;
        this.zzbEp = z;
        this.zzbEq = list;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbao.zza(this, parcel, i);
    }
}
