package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class zzbge extends zza implements zzbgk<String, Integer> {
    public static final Creator<zzbge> CREATOR = new zzbgg();
    private final HashMap<String, Integer> zzaIC;
    private final SparseArray<String> zzaID;
    private final ArrayList<zzbgf> zzaIE;
    private int zzaku;

    public zzbge() {
        this.zzaku = 1;
        this.zzaIC = new HashMap();
        this.zzaID = new SparseArray();
        this.zzaIE = null;
    }

    zzbge(int i, ArrayList<zzbgf> arrayList) {
        this.zzaku = i;
        this.zzaIC = new HashMap();
        this.zzaID = new SparseArray();
        this.zzaIE = null;
        zzd(arrayList);
    }

    private final void zzd(ArrayList<zzbgf> arrayList) {
        ArrayList arrayList2 = arrayList;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            zzbgf com_google_android_gms_internal_zzbgf = (zzbgf) obj;
            zzi(com_google_android_gms_internal_zzbgf.zzaIF, com_google_android_gms_internal_zzbgf.zzaIG);
        }
    }

    public final /* synthetic */ Object convertBack(Object obj) {
        String str = (String) this.zzaID.get(((Integer) obj).intValue());
        return (str == null && this.zzaIC.containsKey("gms_unknown")) ? "gms_unknown" : str;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        List arrayList = new ArrayList();
        for (String str : this.zzaIC.keySet()) {
            arrayList.add(new zzbgf(str, ((Integer) this.zzaIC.get(str)).intValue()));
        }
        zzd.zzc(parcel, 2, arrayList, false);
        zzd.zzI(parcel, zze);
    }

    public final zzbge zzi(String str, int i) {
        this.zzaIC.put(str, Integer.valueOf(i));
        this.zzaID.put(i, str);
        return this;
    }
}
