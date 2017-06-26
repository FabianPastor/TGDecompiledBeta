package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class zzbgo extends zza {
    public static final Creator<zzbgo> CREATOR = new zzbgr();
    final String className;
    private int versionCode;
    private ArrayList<zzbgp> zzaIU;

    zzbgo(int i, String str, ArrayList<zzbgp> arrayList) {
        this.versionCode = i;
        this.className = str;
        this.zzaIU = arrayList;
    }

    zzbgo(String str, Map<String, zzbgi<?, ?>> map) {
        ArrayList arrayList;
        this.versionCode = 1;
        this.className = str;
        if (map == null) {
            arrayList = null;
        } else {
            ArrayList arrayList2 = new ArrayList();
            for (String str2 : map.keySet()) {
                arrayList2.add(new zzbgp(str2, (zzbgi) map.get(str2)));
            }
            arrayList = arrayList2;
        }
        this.zzaIU = arrayList;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zza(parcel, 2, this.className, false);
        zzd.zzc(parcel, 3, this.zzaIU, false);
        zzd.zzI(parcel, zze);
    }

    final HashMap<String, zzbgi<?, ?>> zzrS() {
        HashMap<String, zzbgi<?, ?>> hashMap = new HashMap();
        int size = this.zzaIU.size();
        for (int i = 0; i < size; i++) {
            zzbgp com_google_android_gms_internal_zzbgp = (zzbgp) this.zzaIU.get(i);
            hashMap.put(com_google_android_gms_internal_zzbgp.key, com_google_android_gms_internal_zzbgp.zzaIV);
        }
        return hashMap;
    }
}
