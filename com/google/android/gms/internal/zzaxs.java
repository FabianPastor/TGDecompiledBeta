package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.List;

public class zzaxs extends zza {
    public static final Creator<zzaxs> CREATOR = new zzaxt();
    final int mVersionCode;
    final boolean zzbCn;
    final List<Scope> zzbCo;

    zzaxs(int i, boolean z, List<Scope> list) {
        this.mVersionCode = i;
        this.zzbCn = z;
        this.zzbCo = list;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaxt.zza(this, parcel, i);
    }
}
