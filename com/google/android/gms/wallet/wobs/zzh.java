package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzh extends zza {
    public static final Creator<zzh> CREATOR = new zzi();
    private String zzbOF;
    private int zzbQJ;
    private String zzbQK;
    private double zzbQL;
    private long zzbQM;
    private int zzbQN;

    zzh() {
        this.zzbQN = -1;
        this.zzbQJ = -1;
        this.zzbQL = -1.0d;
    }

    zzh(int i, String str, double d, String str2, long j, int i2) {
        this.zzbQJ = i;
        this.zzbQK = str;
        this.zzbQL = d;
        this.zzbOF = str2;
        this.zzbQM = j;
        this.zzbQN = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbQJ);
        zzd.zza(parcel, 3, this.zzbQK, false);
        zzd.zza(parcel, 4, this.zzbQL);
        zzd.zza(parcel, 5, this.zzbOF, false);
        zzd.zza(parcel, 6, this.zzbQM);
        zzd.zzc(parcel, 7, this.zzbQN);
        zzd.zzI(parcel, zze);
    }
}
