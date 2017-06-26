package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class zzbgn extends zza {
    public static final Creator<zzbgn> CREATOR = new zzbgq();
    private final HashMap<String, Map<String, zzbgi<?, ?>>> zzaIR;
    private final ArrayList<zzbgo> zzaIS = null;
    private final String zzaIT;
    private int zzaku;

    zzbgn(int i, ArrayList<zzbgo> arrayList, String str) {
        this.zzaku = i;
        HashMap hashMap = new HashMap();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            zzbgo com_google_android_gms_internal_zzbgo = (zzbgo) arrayList.get(i2);
            hashMap.put(com_google_android_gms_internal_zzbgo.className, com_google_android_gms_internal_zzbgo.zzrS());
        }
        this.zzaIR = hashMap;
        this.zzaIT = (String) zzbo.zzu(str);
        zzrQ();
    }

    private final void zzrQ() {
        for (String str : this.zzaIR.keySet()) {
            Map map = (Map) this.zzaIR.get(str);
            for (String str2 : map.keySet()) {
                ((zzbgi) map.get(str2)).zza(this);
            }
        }
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : this.zzaIR.keySet()) {
            stringBuilder.append(str).append(":\n");
            Map map = (Map) this.zzaIR.get(str);
            for (String str2 : map.keySet()) {
                stringBuilder.append("  ").append(str2).append(": ");
                stringBuilder.append(map.get(str2));
            }
        }
        return stringBuilder.toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        List arrayList = new ArrayList();
        for (String str : this.zzaIR.keySet()) {
            arrayList.add(new zzbgo(str, (Map) this.zzaIR.get(str)));
        }
        zzd.zzc(parcel, 2, arrayList, false);
        zzd.zza(parcel, 3, this.zzaIT, false);
        zzd.zzI(parcel, zze);
    }

    public final Map<String, zzbgi<?, ?>> zzcJ(String str) {
        return (Map) this.zzaIR.get(str);
    }

    public final String zzrR() {
        return this.zzaIT;
    }
}
