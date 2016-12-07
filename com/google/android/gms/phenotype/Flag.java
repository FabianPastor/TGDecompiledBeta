package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class Flag extends AbstractSafeParcelable implements Comparable<Flag> {
    public static final Creator<Flag> CREATOR = new zzb();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final zza axt = new zza();
    final String Dr;
    final double afA;
    final boolean afy;
    final long axp;
    final byte[] axq;
    public final int axr;
    public final int axs;
    final int mVersionCode;
    public final String name;

    public static class zza implements Comparator<Flag> {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Flag) obj, (Flag) obj2);
        }

        public int zza(Flag flag, Flag flag2) {
            return flag.axs == flag2.axs ? flag.name.compareTo(flag2.name) : flag.axs - flag2.axs;
        }
    }

    Flag(int i, String str, long j, boolean z, double d, String str2, byte[] bArr, int i2, int i3) {
        this.mVersionCode = i;
        this.name = str;
        this.axp = j;
        this.afy = z;
        this.afA = d;
        this.Dr = str2;
        this.axq = bArr;
        this.axr = i2;
        this.axs = i3;
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
        if (this.mVersionCode != flag.mVersionCode || !zzab.equal(this.name, flag.name) || this.axr != flag.axr || this.axs != flag.axs) {
            return false;
        }
        switch (this.axr) {
            case 1:
                return this.axp == flag.axp;
            case 2:
                return this.afy == flag.afy;
            case 3:
                return this.afA == flag.afA;
            case 4:
                return zzab.equal(this.Dr, flag.Dr);
            case 5:
                return Arrays.equals(this.axq, flag.axq);
            default:
                throw new AssertionError("Invalid enum value: " + this.axr);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Flag(");
        stringBuilder.append(this.mVersionCode);
        stringBuilder.append(", ");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        switch (this.axr) {
            case 1:
                stringBuilder.append(this.axp);
                break;
            case 2:
                stringBuilder.append(this.afy);
                break;
            case 3:
                stringBuilder.append(this.afA);
                break;
            case 4:
                stringBuilder.append("'");
                stringBuilder.append(this.Dr);
                stringBuilder.append("'");
                break;
            case 5:
                if (this.axq != null) {
                    stringBuilder.append("'");
                    stringBuilder.append(new String(this.axq, UTF_8));
                    stringBuilder.append("'");
                    break;
                }
                stringBuilder.append("null");
                break;
            default:
                throw new AssertionError("Invalid enum value: " + this.axr);
        }
        stringBuilder.append(", ");
        stringBuilder.append(this.axr);
        stringBuilder.append(", ");
        stringBuilder.append(this.axs);
        stringBuilder.append(")");
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
        compareTo = compare(this.axr, flag.axr);
        if (compareTo != 0) {
            return compareTo;
        }
        switch (this.axr) {
            case 1:
                return compare(this.axp, flag.axp);
            case 2:
                return compare(this.afy, flag.afy);
            case 3:
                return Double.compare(this.afA, flag.afA);
            case 4:
                return compare(this.Dr, flag.Dr);
            case 5:
                if (this.axq == flag.axq) {
                    return 0;
                }
                if (this.axq == null) {
                    return -1;
                }
                if (flag.axq == null) {
                    return 1;
                }
                while (i < Math.min(this.axq.length, flag.axq.length)) {
                    compareTo = compare(this.axq[i], flag.axq[i]);
                    if (compareTo != 0) {
                        return compareTo;
                    }
                    i++;
                }
                return compare(this.axq.length, flag.axq.length);
            default:
                throw new AssertionError("Invalid enum value: " + this.axr);
        }
    }
}
