package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzz;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Configuration extends AbstractSafeParcelable implements Comparable<Configuration> {
    public static final Creator<Configuration> CREATOR = new zza();
    public final int aAs;
    public final Flag[] aAt;
    public final String[] aAu;
    public final Map<String, Flag> aAv = new TreeMap();
    final int mVersionCode;

    Configuration(int i, int i2, Flag[] flagArr, String[] strArr) {
        this.mVersionCode = i;
        this.aAs = i2;
        this.aAt = flagArr;
        for (Flag flag : flagArr) {
            this.aAv.put(flag.name, flag);
        }
        this.aAu = strArr;
        if (this.aAu != null) {
            Arrays.sort(this.aAu);
        }
    }

    public /* synthetic */ int compareTo(Object obj) {
        return zza((Configuration) obj);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Configuration)) {
            return false;
        }
        Configuration configuration = (Configuration) obj;
        return this.mVersionCode == configuration.mVersionCode && this.aAs == configuration.aAs && zzz.equal(this.aAv, configuration.aAv) && Arrays.equals(this.aAu, configuration.aAu);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Configuration(");
        stringBuilder.append(this.mVersionCode);
        stringBuilder.append(", ");
        stringBuilder.append(this.aAs);
        stringBuilder.append(", ");
        stringBuilder.append("(");
        for (Flag append : this.aAv.values()) {
            stringBuilder.append(append);
            stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        stringBuilder.append(", ");
        stringBuilder.append("(");
        if (this.aAu != null) {
            for (String append2 : this.aAu) {
                stringBuilder.append(append2);
                stringBuilder.append(", ");
            }
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(")");
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public int zza(Configuration configuration) {
        return this.aAs - configuration.aAs;
    }
}
