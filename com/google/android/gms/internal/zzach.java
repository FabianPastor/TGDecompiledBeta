package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.internal.zzack.zzb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class zzach extends com.google.android.gms.common.internal.safeparcel.zza implements zzb<String, Integer> {
    public static final Creator<zzach> CREATOR = new zzaci();
    final int mVersionCode;
    private final HashMap<String, Integer> zzaFv;
    private final SparseArray<String> zzaFw;
    private final ArrayList<zza> zzaFx;

    public static final class zza extends com.google.android.gms.common.internal.safeparcel.zza {
        public static final Creator<zza> CREATOR = new zzacj();
        final int versionCode;
        final String zzaFy;
        final int zzaFz;

        zza(int i, String str, int i2) {
            this.versionCode = i;
            this.zzaFy = str;
            this.zzaFz = i2;
        }

        zza(String str, int i) {
            this.versionCode = 1;
            this.zzaFy = str;
            this.zzaFz = i;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzacj.zza(this, parcel, i);
        }
    }

    public zzach() {
        this.mVersionCode = 1;
        this.zzaFv = new HashMap();
        this.zzaFw = new SparseArray();
        this.zzaFx = null;
    }

    zzach(int i, ArrayList<zza> arrayList) {
        this.mVersionCode = i;
        this.zzaFv = new HashMap();
        this.zzaFw = new SparseArray();
        this.zzaFx = null;
        zzh(arrayList);
    }

    private void zzh(ArrayList<zza> arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            zza com_google_android_gms_internal_zzach_zza = (zza) it.next();
            zzj(com_google_android_gms_internal_zzach_zza.zzaFy, com_google_android_gms_internal_zzach_zza.zzaFz);
        }
    }

    public /* synthetic */ Object convertBack(Object obj) {
        return zzd((Integer) obj);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaci.zza(this, parcel, i);
    }

    public String zzd(Integer num) {
        String str = (String) this.zzaFw.get(num.intValue());
        return (str == null && this.zzaFv.containsKey("gms_unknown")) ? "gms_unknown" : str;
    }

    public zzach zzj(String str, int i) {
        this.zzaFv.put(str, Integer.valueOf(i));
        this.zzaFw.put(i, str);
        return this;
    }

    ArrayList<zza> zzxJ() {
        ArrayList<zza> arrayList = new ArrayList();
        for (String str : this.zzaFv.keySet()) {
            arrayList.add(new zza(str, ((Integer) this.zzaFv.get(str)).intValue()));
        }
        return arrayList;
    }
}
