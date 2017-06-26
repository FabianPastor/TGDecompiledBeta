package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;

public final class zze extends zza {
    public static final Creator<zze> CREATOR = new zzf();
    private String zzbQF;
    private String zzbQG;
    private ArrayList<zzc> zzbQH;

    zze() {
        this.zzbQH = new ArrayList();
    }

    zze(String str, String str2, ArrayList<zzc> arrayList) {
        this.zzbQF = str;
        this.zzbQG = str2;
        this.zzbQH = arrayList;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQF, false);
        zzd.zza(parcel, 3, this.zzbQG, false);
        zzd.zzc(parcel, 4, this.zzbQH, false);
        zzd.zzI(parcel, zze);
    }
}
