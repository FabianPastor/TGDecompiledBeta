package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.server.response.FastJsonResponse.zza;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class StringToIntConverter extends AbstractSafeParcelable implements zza<String, Integer> {
    public static final Creator<StringToIntConverter> CREATOR = new zzb();
    private final HashMap<String, Integer> Fb;
    private final SparseArray<String> Fc;
    private final ArrayList<Entry> Fd;
    final int mVersionCode;

    public static final class Entry extends AbstractSafeParcelable {
        public static final Creator<Entry> CREATOR = new zzc();
        final String Fe;
        final int Ff;
        final int versionCode;

        Entry(int i, String str, int i2) {
            this.versionCode = i;
            this.Fe = str;
            this.Ff = i2;
        }

        Entry(String str, int i) {
            this.versionCode = 1;
            this.Fe = str;
            this.Ff = i;
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzc.zza(this, parcel, i);
        }
    }

    public StringToIntConverter() {
        this.mVersionCode = 1;
        this.Fb = new HashMap();
        this.Fc = new SparseArray();
        this.Fd = null;
    }

    StringToIntConverter(int i, ArrayList<Entry> arrayList) {
        this.mVersionCode = i;
        this.Fb = new HashMap();
        this.Fc = new SparseArray();
        this.Fd = null;
        zzh(arrayList);
    }

    private void zzh(ArrayList<Entry> arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            zzj(entry.Fe, entry.Ff);
        }
    }

    public /* synthetic */ Object convertBack(Object obj) {
        return zzd((Integer) obj);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    ArrayList<Entry> zzawy() {
        ArrayList<Entry> arrayList = new ArrayList();
        for (String str : this.Fb.keySet()) {
            arrayList.add(new Entry(str, ((Integer) this.Fb.get(str)).intValue()));
        }
        return arrayList;
    }

    public String zzd(Integer num) {
        String str = (String) this.Fc.get(num.intValue());
        return (str == null && this.Fb.containsKey("gms_unknown")) ? "gms_unknown" : str;
    }

    public StringToIntConverter zzj(String str, int i) {
        this.Fb.put(str, Integer.valueOf(i));
        this.Fc.put(i, str);
        return this;
    }
}
