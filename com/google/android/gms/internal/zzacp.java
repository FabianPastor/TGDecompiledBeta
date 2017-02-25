package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.internal.zzacs.zzb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class zzacp extends com.google.android.gms.common.internal.safeparcel.zza implements zzb<String, Integer> {
    public static final Creator<zzacp> CREATOR = new zzacq();
    private final HashMap<String, Integer> zzaGS;
    private final SparseArray<String> zzaGT;
    private final ArrayList<zza> zzaGU;
    final int zzaiI;

    public static final class zza extends com.google.android.gms.common.internal.safeparcel.zza {
        public static final Creator<zza> CREATOR = new zzacr();
        final int versionCode;
        final String zzaGV;
        final int zzaGW;

        zza(int i, String str, int i2) {
            this.versionCode = i;
            this.zzaGV = str;
            this.zzaGW = i2;
        }

        zza(String str, int i) {
            this.versionCode = 1;
            this.zzaGV = str;
            this.zzaGW = i;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzacr.zza(this, parcel, i);
        }
    }

    public zzacp() {
        this.zzaiI = 1;
        this.zzaGS = new HashMap();
        this.zzaGT = new SparseArray();
        this.zzaGU = null;
    }

    zzacp(int i, ArrayList<zza> arrayList) {
        this.zzaiI = i;
        this.zzaGS = new HashMap();
        this.zzaGT = new SparseArray();
        this.zzaGU = null;
        zzh(arrayList);
    }

    private void zzh(ArrayList<zza> arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zza com_google_android_gms_internal_zzacp_zza = (zza) it.next();
            zzj(com_google_android_gms_internal_zzacp_zza.zzaGV, com_google_android_gms_internal_zzacp_zza.zzaGW);
        }
    }

    public /* synthetic */ Object convertBack(Object obj) {
        return zzd((Integer) obj);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzacq.zza(this, parcel, i);
    }

    public String zzd(Integer num) {
        String str = (String) this.zzaGT.get(num.intValue());
        return (str == null && this.zzaGS.containsKey("gms_unknown")) ? "gms_unknown" : str;
    }

    public zzacp zzj(String str, int i) {
        this.zzaGS.put(str, Integer.valueOf(i));
        this.zzaGT.put(i, str);
        return this;
    }

    ArrayList<zza> zzyq() {
        ArrayList<zza> arrayList = new ArrayList();
        for (String str : this.zzaGS.keySet()) {
            arrayList.add(new zza(str, ((Integer) this.zzaGS.get(str)).intValue()));
        }
        return arrayList;
    }
}
