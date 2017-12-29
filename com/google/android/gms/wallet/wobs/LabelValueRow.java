package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.ArrayList;

public final class LabelValueRow extends zzbfm {
    public static final Creator<LabelValueRow> CREATOR = new zze();
    String zzlfr;
    String zzlfs;
    ArrayList<LabelValue> zzlft;

    LabelValueRow() {
        this.zzlft = new ArrayList();
    }

    LabelValueRow(String str, String str2, ArrayList<LabelValue> arrayList) {
        this.zzlfr = str;
        this.zzlfs = str2;
        this.zzlft = arrayList;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlfr, false);
        zzbfp.zza(parcel, 3, this.zzlfs, false);
        zzbfp.zzc(parcel, 4, this.zzlft, false);
        zzbfp.zzai(parcel, zze);
    }
}
