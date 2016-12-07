package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Configuration extends AbstractSafeParcelable implements Comparable<Configuration> {
    public static final Creator<Configuration> CREATOR = new zza();
    public final int axl;
    public final Flag[] axm;
    public final String[] axn;
    public final Map<String, Flag> axo = new TreeMap();
    final int mVersionCode;

    Configuration(int i, int i2, Flag[] flagArr, String[] strArr) {
        this.mVersionCode = i;
        this.axl = i2;
        this.axm = flagArr;
        for (Flag flag : flagArr) {
            this.axo.put(flag.name, flag);
        }
        this.axn = strArr;
        if (this.axn != null) {
            Arrays.sort(this.axn);
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
        return this.mVersionCode == configuration.mVersionCode && this.axl == configuration.axl && zzab.equal(this.axo, configuration.axo) && Arrays.equals(this.axn, configuration.axn);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Configuration(");
        stringBuilder.append(this.mVersionCode);
        stringBuilder.append(", ");
        stringBuilder.append(this.axl);
        stringBuilder.append(", ");
        stringBuilder.append("(");
        for (Flag append : this.axo.values()) {
            stringBuilder.append(append);
            stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        stringBuilder.append(", ");
        stringBuilder.append("(");
        if (this.axn != null) {
            for (String append2 : this.axn) {
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
        return this.axl - configuration.axl;
    }
}
