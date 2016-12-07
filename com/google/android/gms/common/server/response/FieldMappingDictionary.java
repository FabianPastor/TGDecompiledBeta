package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.server.response.FastJsonResponse.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FieldMappingDictionary extends AbstractSafeParcelable {
    public static final Creator<FieldMappingDictionary> CREATOR = new zzc();
    private final HashMap<String, Map<String, Field<?, ?>>> Fq;
    private final ArrayList<Entry> Fr = null;
    private final String Fs;
    final int mVersionCode;

    public static class Entry extends AbstractSafeParcelable {
        public static final Creator<Entry> CREATOR = new zzd();
        final ArrayList<FieldMapPair> Ft;
        final String className;
        final int versionCode;

        Entry(int i, String str, ArrayList<FieldMapPair> arrayList) {
            this.versionCode = i;
            this.className = str;
            this.Ft = arrayList;
        }

        Entry(String str, Map<String, Field<?, ?>> map) {
            this.versionCode = 1;
            this.className = str;
            this.Ft = zzaw(map);
        }

        private static ArrayList<FieldMapPair> zzaw(Map<String, Field<?, ?>> map) {
            if (map == null) {
                return null;
            }
            ArrayList<FieldMapPair> arrayList = new ArrayList();
            for (String str : map.keySet()) {
                arrayList.add(new FieldMapPair(str, (Field) map.get(str)));
            }
            return arrayList;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzd.zza(this, parcel, i);
        }

        HashMap<String, Field<?, ?>> zzaxo() {
            HashMap<String, Field<?, ?>> hashMap = new HashMap();
            int size = this.Ft.size();
            for (int i = 0; i < size; i++) {
                FieldMapPair fieldMapPair = (FieldMapPair) this.Ft.get(i);
                hashMap.put(fieldMapPair.zzcb, fieldMapPair.Fu);
            }
            return hashMap;
        }
    }

    public static class FieldMapPair extends AbstractSafeParcelable {
        public static final Creator<FieldMapPair> CREATOR = new zzb();
        final Field<?, ?> Fu;
        final int versionCode;
        final String zzcb;

        FieldMapPair(int i, String str, Field<?, ?> field) {
            this.versionCode = i;
            this.zzcb = str;
            this.Fu = field;
        }

        FieldMapPair(String str, Field<?, ?> field) {
            this.versionCode = 1;
            this.zzcb = str;
            this.Fu = field;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzb.zza(this, parcel, i);
        }
    }

    FieldMappingDictionary(int i, ArrayList<Entry> arrayList, String str) {
        this.mVersionCode = i;
        this.Fq = zzi(arrayList);
        this.Fs = (String) zzaa.zzy(str);
        zzaxl();
    }

    private static HashMap<String, Map<String, Field<?, ?>>> zzi(ArrayList<Entry> arrayList) {
        HashMap<String, Map<String, Field<?, ?>>> hashMap = new HashMap();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Entry entry = (Entry) arrayList.get(i);
            hashMap.put(entry.className, entry.zzaxo());
        }
        return hashMap;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : this.Fq.keySet()) {
            stringBuilder.append(str).append(":\n");
            Map map = (Map) this.Fq.get(str);
            for (String str2 : map.keySet()) {
                stringBuilder.append("  ").append(str2).append(": ");
                stringBuilder.append(map.get(str2));
            }
        }
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public void zzaxl() {
        for (String str : this.Fq.keySet()) {
            Map map = (Map) this.Fq.get(str);
            for (String str2 : map.keySet()) {
                ((Field) map.get(str2)).zza(this);
            }
        }
    }

    ArrayList<Entry> zzaxm() {
        ArrayList<Entry> arrayList = new ArrayList();
        for (String str : this.Fq.keySet()) {
            arrayList.add(new Entry(str, (Map) this.Fq.get(str)));
        }
        return arrayList;
    }

    public String zzaxn() {
        return this.Fs;
    }

    public Map<String, Field<?, ?>> zzig(String str) {
        return (Map) this.Fq.get(str);
    }
}
