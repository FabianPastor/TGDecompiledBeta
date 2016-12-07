package com.google.android.gms.common.server.response;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.server.response.FastJsonResponse.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FieldMappingDictionary extends AbstractSafeParcelable {
    public static final zzc CREATOR = new zzc();
    private final HashMap<String, Map<String, Field<?, ?>>> DD;
    private final ArrayList<Entry> DE = null;
    private final String DF;
    private final int mVersionCode;

    public static class Entry extends AbstractSafeParcelable {
        public static final zzd CREATOR = new zzd();
        final ArrayList<FieldMapPair> DG;
        final String className;
        final int versionCode;

        Entry(int i, String str, ArrayList<FieldMapPair> arrayList) {
            this.versionCode = i;
            this.className = str;
            this.DG = arrayList;
        }

        Entry(String str, Map<String, Field<?, ?>> map) {
            this.versionCode = 1;
            this.className = str;
            this.DG = zzau(map);
        }

        private static ArrayList<FieldMapPair> zzau(Map<String, Field<?, ?>> map) {
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
            zzd com_google_android_gms_common_server_response_zzd = CREATOR;
            zzd.zza(this, parcel, i);
        }

        HashMap<String, Field<?, ?>> zzawh() {
            HashMap<String, Field<?, ?>> hashMap = new HashMap();
            int size = this.DG.size();
            for (int i = 0; i < size; i++) {
                FieldMapPair fieldMapPair = (FieldMapPair) this.DG.get(i);
                hashMap.put(fieldMapPair.zzcb, fieldMapPair.DH);
            }
            return hashMap;
        }
    }

    public static class FieldMapPair extends AbstractSafeParcelable {
        public static final zzb CREATOR = new zzb();
        final Field<?, ?> DH;
        final int versionCode;
        final String zzcb;

        FieldMapPair(int i, String str, Field<?, ?> field) {
            this.versionCode = i;
            this.zzcb = str;
            this.DH = field;
        }

        FieldMapPair(String str, Field<?, ?> field) {
            this.versionCode = 1;
            this.zzcb = str;
            this.DH = field;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzb com_google_android_gms_common_server_response_zzb = CREATOR;
            zzb.zza(this, parcel, i);
        }
    }

    FieldMappingDictionary(int i, ArrayList<Entry> arrayList, String str) {
        this.mVersionCode = i;
        this.DD = zzi(arrayList);
        this.DF = (String) zzac.zzy(str);
        zzawe();
    }

    private static HashMap<String, Map<String, Field<?, ?>>> zzi(ArrayList<Entry> arrayList) {
        HashMap<String, Map<String, Field<?, ?>>> hashMap = new HashMap();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Entry entry = (Entry) arrayList.get(i);
            hashMap.put(entry.className, entry.zzawh());
        }
        return hashMap;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : this.DD.keySet()) {
            stringBuilder.append(str).append(":\n");
            Map map = (Map) this.DD.get(str);
            for (String str2 : map.keySet()) {
                stringBuilder.append("  ").append(str2).append(": ");
                stringBuilder.append(map.get(str2));
            }
        }
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc com_google_android_gms_common_server_response_zzc = CREATOR;
        zzc.zza(this, parcel, i);
    }

    public void zzawe() {
        for (String str : this.DD.keySet()) {
            Map map = (Map) this.DD.get(str);
            for (String str2 : map.keySet()) {
                ((Field) map.get(str2)).zza(this);
            }
        }
    }

    ArrayList<Entry> zzawf() {
        ArrayList<Entry> arrayList = new ArrayList();
        for (String str : this.DD.keySet()) {
            arrayList.add(new Entry(str, (Map) this.DD.get(str)));
        }
        return arrayList;
    }

    public String zzawg() {
        return this.DF;
    }

    public Map<String, Field<?, ?>> zzie(String str) {
        return (Map) this.DD.get(str);
    }
}
