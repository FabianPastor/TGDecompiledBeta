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
    public static final zza zzbBK = new zza();
    public final String name;
    final String zzaGV;
    final long zzbBG;
    final byte[] zzbBH;
    public final int zzbBI;
    public final int zzbBJ;
    final boolean zzbhm;
    final double zzbho;

    public static class zza implements Comparator<zzayz> {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((zzayz) obj, (zzayz) obj2);
        }

        public int zza(zzayz com_google_android_gms_internal_zzayz, zzayz com_google_android_gms_internal_zzayz2) {
            return com_google_android_gms_internal_zzayz.zzbBJ == com_google_android_gms_internal_zzayz2.zzbBJ ? com_google_android_gms_internal_zzayz.name.compareTo(com_google_android_gms_internal_zzayz2.name) : com_google_android_gms_internal_zzayz.zzbBJ - com_google_android_gms_internal_zzayz2.zzbBJ;
        }
    }

    public zzayz(String str, long j, boolean z, double d, String str2, byte[] bArr, int i, int i2) {
        this.name = str;
        this.zzbBG = j;
        this.zzbhm = z;
        this.zzbho = d;
        this.zzaGV = str2;
        this.zzbBH = bArr;
        this.zzbBI = i;
        this.zzbBJ = i2;
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
        if (!zzaa.equal(this.name, com_google_android_gms_internal_zzayz.name) || this.zzbBI != com_google_android_gms_internal_zzayz.zzbBI || this.zzbBJ != com_google_android_gms_internal_zzayz.zzbBJ) {
            return false;
        }
        switch (this.zzbBI) {
            case 1:
                return this.zzbBG == com_google_android_gms_internal_zzayz.zzbBG;
            case 2:
                return this.zzbhm == com_google_android_gms_internal_zzayz.zzbhm;
            case 3:
                return this.zzbho == com_google_android_gms_internal_zzayz.zzbho;
            case 4:
                return zzaa.equal(this.zzaGV, com_google_android_gms_internal_zzayz.zzaGV);
            case 5:
                return Arrays.equals(this.zzbBH, com_google_android_gms_internal_zzayz.zzbBH);
            default:
                throw new AssertionError("Invalid enum value: " + this.zzbBI);
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
        compareTo = compare(this.zzbBI, com_google_android_gms_internal_zzayz.zzbBI);
        if (compareTo != 0) {
            return compareTo;
        }
        switch (this.zzbBI) {
            case 1:
                return compare(this.zzbBG, com_google_android_gms_internal_zzayz.zzbBG);
            case 2:
                return compare(this.zzbhm, com_google_android_gms_internal_zzayz.zzbhm);
            case 3:
                return Double.compare(this.zzbho, com_google_android_gms_internal_zzayz.zzbho);
            case 4:
                return compare(this.zzaGV, com_google_android_gms_internal_zzayz.zzaGV);
            case 5:
                if (this.zzbBH == com_google_android_gms_internal_zzayz.zzbBH) {
                    return 0;
                }
                if (this.zzbBH == null) {
                    return -1;
                }
                if (com_google_android_gms_internal_zzayz.zzbBH == null) {
                    return 1;
                }
                while (i < Math.min(this.zzbBH.length, com_google_android_gms_internal_zzayz.zzbBH.length)) {
                    compareTo = compare(this.zzbBH[i], com_google_android_gms_internal_zzayz.zzbBH[i]);
                    if (compareTo != 0) {
                        return compareTo;
                    }
                    i++;
                }
                return compare(this.zzbBH.length, com_google_android_gms_internal_zzayz.zzbBH.length);
            default:
                throw new AssertionError("Invalid enum value: " + this.zzbBI);
        }
    }

    public String zza(StringBuilder stringBuilder) {
        stringBuilder.append("Flag(");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        switch (this.zzbBI) {
            case 1:
                stringBuilder.append(this.zzbBG);
                break;
            case 2:
                stringBuilder.append(this.zzbhm);
                break;
            case 3:
                stringBuilder.append(this.zzbho);
                break;
            case 4:
                stringBuilder.append("'");
                stringBuilder.append(this.zzaGV);
                stringBuilder.append("'");
                break;
            case 5:
                if (this.zzbBH != null) {
                    stringBuilder.append("'");
                    stringBuilder.append(new String(this.zzbBH, UTF_8));
                    stringBuilder.append("'");
                    break;
                }
                stringBuilder.append("null");
                break;
            default:
                String str = this.name;
                throw new AssertionError(new StringBuilder(String.valueOf(str).length() + 27).append("Invalid type: ").append(str).append(", ").append(this.zzbBI).toString());
        }
        stringBuilder.append(", ");
        stringBuilder.append(this.zzbBI);
        stringBuilder.append(", ");
        stringBuilder.append(this.zzbBJ);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
