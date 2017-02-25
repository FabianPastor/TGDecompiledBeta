package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.util.zzb;
import java.util.ArrayList;

public final class zzd extends zza {
    public static final Creator<zzd> CREATOR = new zze();
    String zzbSA;
    ArrayList<zzb> zzbSB;
    String zzbSz;

    zzd() {
        this.zzbSB = zzb.zzyY();
    }

    zzd(String str, String str2, ArrayList<zzb> arrayList) {
        this.zzbSz = str;
        this.zzbSA = str2;
        this.zzbSB = arrayList;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze.zza(this, parcel, i);
    }
}
