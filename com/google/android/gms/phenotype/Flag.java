package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzz;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class Flag extends AbstractSafeParcelable implements Comparable<Flag> {
    public static final Creator<Flag> CREATOR = new zzb();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final zza aAA = new zza();
    final String Fe;
    final long aAw;
    final byte[] aAx;
    public final int aAy;
    public final int aAz;
    final boolean ahI;
    final double ahK;
    final int mVersionCode;
    public final String name;

    public static class zza implements Comparator<Flag> {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Flag) obj, (Flag) obj2);
        }

        public int zza(Flag flag, Flag flag2) {
            return flag.aAz == flag2.aAz ? flag.name.compareTo(flag2.name) : flag.aAz - flag2.aAz;
        }
    }

    Flag(int i, String str, long j, boolean z, double d, String str2, byte[] bArr, int i2, int i3) {
        this.mVersionCode = i;
        this.name = str;
        this.aAw = j;
        this.ahI = z;
        this.ahK = d;
        this.Fe = str2;
        this.aAx = bArr;
        this.aAy = i2;
        this.aAz = i3;
    }

    private static int compare(byte b, byte b2) {
        return b - b2;
    }

    private static int compare(int i, int i2) {
        return i < i2 ? -1 : i == i2 ? 0 : 1;
    }

    private static int compare(long j, long j2) {
        return j < j2 ? -1 : j == j2 ? 0 : 1;
    }

    private static int compare(String str, String str2) {
        return str == str2 ? 0 : str == null ? -1 : str2 == null ? 1 : str.compareTo(str2);
    }

    private static int compare(boolean z, boolean z2) {
        return z == z2 ? 0 : z ? 1 : -1;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return zza((Flag) obj);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Flag)) {
            return false;
        }
        Flag flag = (Flag) obj;
        if (this.mVersionCode != flag.mVersionCode || !zzz.equal(this.name, flag.name) || this.aAy != flag.aAy || this.aAz != flag.aAz) {
            return false;
        }
        switch (this.aAy) {
            case 1:
                return this.aAw == flag.aAw;
            case 2:
                return this.ahI == flag.ahI;
            case 3:
                return this.ahK == flag.ahK;
            case 4:
                return zzz.equal(this.Fe, flag.Fe);
            case 5:
                return Arrays.equals(this.aAx, flag.aAx);
            default:
                throw new AssertionError("Invalid enum value: " + this.aAy);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        zza(stringBuilder);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }

    public int zza(Flag flag) {
        int i = 0;
        int compareTo = this.name.compareTo(flag.name);
        if (compareTo != 0) {
            return compareTo;
        }
        compareTo = compare(this.aAy, flag.aAy);
        if (compareTo != 0) {
            return compareTo;
        }
        switch (this.aAy) {
            case 1:
                return compare(this.aAw, flag.aAw);
            case 2:
                return compare(this.ahI, flag.ahI);
            case 3:
                return Double.compare(this.ahK, flag.ahK);
            case 4:
                return compare(this.Fe, flag.Fe);
            case 5:
                if (this.aAx == flag.aAx) {
                    return 0;
                }
                if (this.aAx == null) {
                    return -1;
                }
                if (flag.aAx == null) {
                    return 1;
                }
                while (i < Math.min(this.aAx.length, flag.aAx.length)) {
                    compareTo = compare(this.aAx[i], flag.aAx[i]);
                    if (compareTo != 0) {
                        return compareTo;
                    }
                    i++;
                }
                return compare(this.aAx.length, flag.aAx.length);
            default:
                throw new AssertionError("Invalid enum value: " + this.aAy);
        }
    }

    public String zza(StringBuilder stringBuilder) {
        stringBuilder.append("Flag(");
        stringBuilder.append(this.mVersionCode);
        stringBuilder.append(", ");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        switch (this.aAy) {
            case 1:
                stringBuilder.append(this.aAw);
                break;
            case 2:
                stringBuilder.append(this.ahI);
                break;
            case 3:
                stringBuilder.append(this.ahK);
                break;
            case 4:
                stringBuilder.append("'");
                stringBuilder.append(this.Fe);
                stringBuilder.append("'");
                break;
            case 5:
                if (this.aAx != null) {
                    stringBuilder.append("'");
                    stringBuilder.append(new String(this.aAx, UTF_8));
                    stringBuilder.append("'");
                    break;
                }
                stringBuilder.append("null");
                break;
            default:
                String str = this.name;
                throw new AssertionError(new StringBuilder(String.valueOf(str).length() + 27).append("Invalid type: ").append(str).append(", ").append(this.aAy).toString());
        }
        stringBuilder.append(", ");
        stringBuilder.append(this.aAy);
        stringBuilder.append(", ");
        stringBuilder.append(this.aAz);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
