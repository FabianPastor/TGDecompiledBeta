package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzac;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class zzaco extends com.google.android.gms.common.internal.safeparcel.zza {
    public static final Creator<zzaco> CREATOR = new zzacp();
    final int mVersionCode;
    private final HashMap<String, Map<String, com.google.android.gms.internal.zzack.zza<?, ?>>> zzaFK;
    private final ArrayList<zza> zzaFL = null;
    private final String zzaFM;

    public static class zza extends com.google.android.gms.common.internal.safeparcel.zza {
        public static final Creator<zza> CREATOR = new zzacq();
        final String className;
        final int versionCode;
        final ArrayList<zzb> zzaFN;

        zza(int i, String str, ArrayList<zzb> arrayList) {
            this.versionCode = i;
            this.className = str;
            this.zzaFN = arrayList;
        }

        zza(String str, Map<String, com.google.android.gms.internal.zzack.zza<?, ?>> map) {
            this.versionCode = 1;
            this.className = str;
            this.zzaFN = zzW(map);
        }

        private static ArrayList<zzb> zzW(Map<String, com.google.android.gms.internal.zzack.zza<?, ?>> map) {
            if (map == null) {
                return null;
            }
            ArrayList<zzb> arrayList = new ArrayList();
            for (String str : map.keySet()) {
                arrayList.add(new zzb(str, (com.google.android.gms.internal.zzack.zza) map.get(str)));
            }
            return arrayList;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzacq.zza(this, parcel, i);
        }

        HashMap<String, com.google.android.gms.internal.zzack.zza<?, ?>> zzxZ() {
            HashMap<String, com.google.android.gms.internal.zzack.zza<?, ?>> hashMap = new HashMap();
            int size = this.zzaFN.size();
            for (int i = 0; i < size; i++) {
                zzb com_google_android_gms_internal_zzaco_zzb = (zzb) this.zzaFN.get(i);
                hashMap.put(com_google_android_gms_internal_zzaco_zzb.zzaA, com_google_android_gms_internal_zzaco_zzb.zzaFO);
            }
            return hashMap;
        }
    }

    public static class zzb extends com.google.android.gms.common.internal.safeparcel.zza {
        public static final Creator<zzb> CREATOR = new zzacn();
        final int versionCode;
        final String zzaA;
        final com.google.android.gms.internal.zzack.zza<?, ?> zzaFO;

        zzb(int i, String str, com.google.android.gms.internal.zzack.zza<?, ?> com_google_android_gms_internal_zzack_zza___) {
            this.versionCode = i;
            this.zzaA = str;
            this.zzaFO = com_google_android_gms_internal_zzack_zza___;
        }

        zzb(String str, com.google.android.gms.internal.zzack.zza<?, ?> com_google_android_gms_internal_zzack_zza___) {
            this.versionCode = 1;
            this.zzaA = str;
            this.zzaFO = com_google_android_gms_internal_zzack_zza___;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzacn.zza(this, parcel, i);
        }
    }

    zzaco(int i, ArrayList<zza> arrayList, String str) {
        this.mVersionCode = i;
        this.zzaFK = zzi(arrayList);
        this.zzaFM = (String) zzac.zzw(str);
        zzxW();
    }

    private static HashMap<String, Map<String, com.google.android.gms.internal.zzack.zza<?, ?>>> zzi(ArrayList<zza> arrayList) {
        HashMap<String, Map<String, com.google.android.gms.internal.zzack.zza<?, ?>>> hashMap = new HashMap();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            zza com_google_android_gms_internal_zzaco_zza = (zza) arrayList.get(i);
            hashMap.put(com_google_android_gms_internal_zzaco_zza.className, com_google_android_gms_internal_zzaco_zza.zzxZ());
        }
        return hashMap;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : this.zzaFK.keySet()) {
            stringBuilder.append(str).append(":\n");
            Map map = (Map) this.zzaFK.get(str);
            for (String str2 : map.keySet()) {
                stringBuilder.append("  ").append(str2).append(": ");
                stringBuilder.append(map.get(str2));
            }
        }
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzacp.zza(this, parcel, i);
    }

    public Map<String, com.google.android.gms.internal.zzack.zza<?, ?>> zzdA(String str) {
        return (Map) this.zzaFK.get(str);
    }

    public void zzxW() {
        for (String str : this.zzaFK.keySet()) {
            Map map = (Map) this.zzaFK.get(str);
            for (String str2 : map.keySet()) {
                ((com.google.android.gms.internal.zzack.zza) map.get(str2)).zza(this);
            }
        }
    }

    ArrayList<zza> zzxX() {
        ArrayList<zza> arrayList = new ArrayList();
        for (String str : this.zzaFK.keySet()) {
            arrayList.add(new zza(str, (Map) this.zzaFK.get(str)));
        }
        return arrayList;
    }

    public String zzxY() {
        return this.zzaFM;
    }
}
