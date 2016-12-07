package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.server.response.FastJsonResponse.zza;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class StringToIntConverter extends AbstractSafeParcelable implements zza<String, Integer> {
    public static final zzb CREATOR = new zzb();
    private final HashMap<String, Integer> Do;
    private final SparseArray<String> Dp;
    private final ArrayList<Entry> Dq;
    private final int mVersionCode;

    public static final class Entry extends AbstractSafeParcelable {
        public static final zzc CREATOR = new zzc();
        final String Dr;
        final int Ds;
        final int versionCode;

        Entry(int i, String str, int i2) {
            this.versionCode = i;
            this.Dr = str;
            this.Ds = i2;
        }

        Entry(String str, int i) {
            this.versionCode = 1;
            this.Dr = str;
            this.Ds = i;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzc com_google_android_gms_common_server_converter_zzc = CREATOR;
            zzc.zza(this, parcel, i);
        }
    }

    public StringToIntConverter() {
        this.mVersionCode = 1;
        this.Do = new HashMap();
        this.Dp = new SparseArray();
        this.Dq = null;
    }

    StringToIntConverter(int i, ArrayList<Entry> arrayList) {
        this.mVersionCode = i;
        this.Do = new HashMap();
        this.Dp = new SparseArray();
        this.Dq = null;
        zzh(arrayList);
    }

    private void zzh(ArrayList<Entry> arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            zzj(entry.Dr, entry.Ds);
        }
    }

    public /* synthetic */ Object convertBack(Object obj) {
        return zzd((Integer) obj);
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb com_google_android_gms_common_server_converter_zzb = CREATOR;
        zzb.zza(this, parcel, i);
    }

    ArrayList<Entry> zzavp() {
        ArrayList<Entry> arrayList = new ArrayList();
        for (String str : this.Do.keySet()) {
            arrayList.add(new Entry(str, ((Integer) this.Do.get(str)).intValue()));
        }
        return arrayList;
    }

    public int zzavq() {
        return 7;
    }

    public int zzavr() {
        return 0;
    }

    public String zzd(Integer num) {
        String str = (String) this.Dp.get(num.intValue());
        return (str == null && this.Do.containsKey("gms_unknown")) ? "gms_unknown" : str;
    }

    public StringToIntConverter zzj(String str, int i) {
        this.Do.put(str, Integer.valueOf(i));
        this.Dp.put(i, str);
        return this;
    }
}
