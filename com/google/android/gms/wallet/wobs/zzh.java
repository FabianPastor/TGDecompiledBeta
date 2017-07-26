package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzh extends zza {
    public static final Creator<zzh> CREATOR = new zzi();
    private String zzbOH;
    private int zzbQL;
    private String zzbQM;
    private double zzbQN;
    private long zzbQO;
    private int zzbQP;

    zzh() {
        this.zzbQP = -1;
        this.zzbQL = -1;
        this.zzbQN = -1.0d;
    }

    zzh(int i, String str, double d, String str2, long j, int i2) {
        this.zzbQL = i;
        this.zzbQM = str;
        this.zzbQN = d;
        this.zzbOH = str2;
        this.zzbQO = j;
        this.zzbQP = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbQL);
        zzd.zza(parcel, 3, this.zzbQM, false);
        zzd.zza(parcel, 4, this.zzbQN);
        zzd.zza(parcel, 5, this.zzbOH, false);
        zzd.zza(parcel, 6, this.zzbQO);
        zzd.zzc(parcel, 7, this.zzbQP);
        zzd.zzI(parcel, zze);
    }
}
