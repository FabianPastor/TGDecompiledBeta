package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzaa;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class zzayz extends com.google.android.gms.common.internal.safeparcel.zza implements Comparable<zzayz> {
    public static final Creator<zzayz> CREATOR = new zzaza();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final zza zzbBG = new zza();
    public final String name;
    final String zzaGV;
    final long zzbBC;
    final byte[] zzbBD;
    public final int zzbBE;
    public final int zzbBF;
    final boolean zzbhn;
    final double zzbhp;

    public static class zza implements Comparator<zzayz> {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((zzayz) obj, (zzayz) obj2);
        }

        public int zza(zzayz com_google_android_gms_internal_zzayz, zzayz com_google_android_gms_internal_zzayz2) {
            return com_google_android_gms_internal_zzayz.zzbBF == com_google_android_gms_internal_zzayz2.zzbBF ? com_google_android_gms_internal_zzayz.name.compareTo(com_google_android_gms_internal_zzayz2.name) : com_google_android_gms_internal_zzayz.zzbBF - com_google_android_gms_internal_zzayz2.zzbBF;
        }
    }

    public zzayz(String str, long j, boolean z, double d, String str2, byte[] bArr, int i, int i2) {
        this.name = str;
        this.zzbBC = j;
        this.zzbhn = z;
        this.zzbhp = d;
        this.zzaGV = str2;
        this.zzbBD = bArr;
        this.zzbBE = i;
        this.zzbBF = i2;
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
        return zza((zzayz) obj);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof zzayz)) {
            return false;
        }
        zzayz com_google_android_gms_internal_zzayz = (zzayz) obj;
        if (!zzaa.equal(this.name, com_google_android_gms_internal_zzayz.name) || this.zzbBE != com_google_android_gms_internal_zzayz.zzbBE || this.zzbBF != com_google_android_gms_internal_zzayz.zzbBF) {
            return false;
        }
        switch (this.zzbBE) {
            case 1:
                return this.zzbBC == com_google_android_gms_internal_zzayz.zzbBC;
            case 2:
                return this.zzbhn == com_google_android_gms_internal_zzayz.zzbhn;
            case 3:
                return this.zzbhp == com_google_android_gms_internal_zzayz.zzbhp;
            case 4:
                return zzaa.equal(this.zzaGV, com_google_android_gms_internal_zzayz.zzaGV);
            case 5:
                return Arrays.equals(this.zzbBD, com_google_android_gms_internal_zzayz.zzbBD);
            default:
                throw new AssertionError("Invalid enum value: " + this.zzbBE);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        zza(stringBuilder);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaza.zza(this, parcel, i);
    }

    public int zza(zzayz com_google_android_gms_internal_zzayz) {
        int i = 0;
        int compareTo = this.name.compareTo(com_google_android_gms_internal_zzayz.name);
        if (compareTo != 0) {
            return compareTo;
        }
        compareTo = compare(this.zzbBE, com_google_android_gms_internal_zzayz.zzbBE);
        if (compareTo != 0) {
            return compareTo;
        }
        switch (this.zzbBE) {
            case 1:
                return compare(this.zzbBC, com_google_android_gms_internal_zzayz.zzbBC);
            case 2:
                return compare(this.zzbhn, com_google_android_gms_internal_zzayz.zzbhn);
            case 3:
                return Double.compare(this.zzbhp, com_google_android_gms_internal_zzayz.zzbhp);
            case 4:
                return compare(this.zzaGV, com_google_android_gms_internal_zzayz.zzaGV);
            case 5:
                if (this.zzbBD == com_google_android_gms_internal_zzayz.zzbBD) {
                    return 0;
                }
                if (this.zzbBD == null) {
                    return -1;
                }
                if (com_google_android_gms_internal_zzayz.zzbBD == null) {
                    return 1;
                }
                while (i < Math.min(this.zzbBD.length, com_google_android_gms_internal_zzayz.zzbBD.length)) {
                    compareTo = compare(this.zzbBD[i], com_google_android_gms_internal_zzayz.zzbBD[i]);
                    if (compareTo != 0) {
                        return compareTo;
                    }
                    i++;
                }
                return compare(this.zzbBD.length, com_google_android_gms_internal_zzayz.zzbBD.length);
            default:
                throw new AssertionError("Invalid enum value: " + this.zzbBE);
        }
    }

    public String zza(StringBuilder stringBuilder) {
        stringBuilder.append("Flag(");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        switch (this.zzbBE) {
            case 1:
                stringBuilder.append(this.zzbBC);
                break;
            case 2:
                stringBuilder.append(this.zzbhn);
                break;
            case 3:
                stringBuilder.append(this.zzbhp);
                break;
            case 4:
                stringBuilder.append("'");
                stringBuilder.append(this.zzaGV);
                stringBuilder.append("'");
                break;
            case 5:
                if (this.zzbBD != null) {
                    stringBuilder.append("'");
                    stringBuilder.append(new String(this.zzbBD, UTF_8));
                    stringBuilder.append("'");
                    break;
                }
                stringBuilder.append("null");
                break;
            default:
                String str = this.name;
                throw new AssertionError(new StringBuilder(String.valueOf(str).length() + 27).append("Invalid type: ").append(str).append(", ").append(this.zzbBE).toString());
        }
        stringBuilder.append(", ");
        stringBuilder.append(this.zzbBE);
        stringBuilder.append(", ");
        stringBuilder.append(this.zzbBF);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
