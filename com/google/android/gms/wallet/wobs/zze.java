package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;

public final class zze extends zza {
    public static final Creator<zze> CREATOR = new zzf();
    private String zzbQH;
    private String zzbQI;
    private ArrayList<zzc> zzbQJ;

    zze() {
        this.zzbQJ = new ArrayList();
    }

    zze(String str, String str2, ArrayList<zzc> arrayList) {
        this.zzbQH = str;
        this.zzbQI = str2;
        this.zzbQJ = arrayList;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbQH, false);
        zzd.zza(parcel, 3, this.zzbQI, false);
        zzd.zzc(parcel, 4, this.zzbQJ, false);
        zzd.zzI(parcel, zze);
    }
}
