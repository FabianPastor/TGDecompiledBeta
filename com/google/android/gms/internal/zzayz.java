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
    public static final zza zzbBJ = new zza();
    public final String name;
    final String zzaGV;
    final long zzbBF;
    final byte[] zzbBG;
    public final int zzbBH;
    public final int zzbBI;
    final boolean zzbhn;
    final double zzbhp;

    public static class zza implements Comparator<zzayz> {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((zzayz) obj, (zzayz) obj2);
        }

        public int zza(zzayz com_google_android_gms_internal_zzayz, zzayz com_google_android_gms_internal_zzayz2) {
            return com_google_android_gms_internal_zzayz.zzbBI == com_google_android_gms_internal_zzayz2.zzbBI ? com_google_android_gms_internal_zzayz.name.compareTo(com_google_android_gms_internal_zzayz2.name) : com_google_android_gms_internal_zzayz.zzbBI - com_google_android_gms_internal_zzayz2.zzbBI;
        }
    }

    public zzayz(String str, long j, boolean z, double d, String str2, byte[] bArr, int i, int i2) {
        this.name = str;
        this.zzbBF = j;
        this.zzbhn = z;
        this.zzbhp = d;
        this.zzaGV = str2;
        this.zzbBG = bArr;
        this.zzbBH = i;
        this.zzbBI = i2;
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
        if (!zzaa.equal(this.name, com_google_android_gms_internal_zzayz.name) || this.zzbBH != com_google_android_gms_internal_zzayz.zzbBH || this.zzbBI != com_google_android_gms_internal_zzayz.zzbBI) {
            return false;
        }
        switch (this.zzbBH) {
            case 1:
                return this.zzbBF == com_google_android_gms_internal_zzayz.zzbBF;
            case 2:
                return this.zzbhn == com_google_android_gms_internal_zzayz.zzbhn;
            case 3:
                return this.zzbhp == com_google_android_gms_internal_zzayz.zzbhp;
            case 4:
                return zzaa.equal(this.zzaGV, com_google_android_gms_internal_zzayz.zzaGV);
            case 5:
                return Arrays.equals(this.zzbBG, com_google_android_gms_internal_zzayz.zzbBG);
            default:
                throw new AssertionError("Invalid enum value: " + this.zzbBH);
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
        compareTo = compare(this.zzbBH, com_google_android_gms_internal_zzayz.zzbBH);
        if (compareTo != 0) {
            return compareTo;
        }
        switch (this.zzbBH) {
            case 1:
                return compare(this.zzbBF, com_google_android_gms_internal_zzayz.zzbBF);
            case 2:
                return compare(this.zzbhn, com_google_android_gms_internal_zzayz.zzbhn);
            case 3:
                return Double.compare(this.zzbhp, com_google_android_gms_internal_zzayz.zzbhp);
            case 4:
                return compare(this.zzaGV, com_google_android_gms_internal_zzayz.zzaGV);
            case 5:
                if (this.zzbBG == com_google_android_gms_internal_zzayz.zzbBG) {
                    return 0;
                }
                if (this.zzbBG == null) {
                    return -1;
                }
                if (com_google_android_gms_internal_zzayz.zzbBG == null) {
                    return 1;
                }
                while (i < Math.min(this.zzbBG.length, com_google_android_gms_internal_zzayz.zzbBG.length)) {
                    compareTo = compare(this.zzbBG[i], com_google_android_gms_internal_zzayz.zzbBG[i]);
                    if (compareTo != 0) {
                        return compareTo;
                    }
                    i++;
                }
                return compare(this.zzbBG.length, com_google_android_gms_internal_zzayz.zzbBG.length);
            default:
                throw new AssertionError("Invalid enum value: " + this.zzbBH);
        }
    }

    public String zza(StringBuilder stringBuilder) {
        stringBuilder.append("Flag(");
        stringBuilder.append(this.name);
        stringBuilder.append(", ");
        switch (this.zzbBH) {
            case 1:
                stringBuilder.append(this.zzbBF);
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
                if (this.zzbBG != null) {
                    stringBuilder.append("'");
                    stringBuilder.append(new String(this.zzbBG, UTF_8));
                    stringBuilder.append("'");
                    break;
                }
                stringBuilder.append("null");
                break;
            default:
                String str = this.name;
                throw new AssertionError(new StringBuilder(String.valueOf(str).length() + 27).append("Invalid type: ").append(str).append(", ").append(this.zzbBH).toString());
        }
        stringBuilder.append(", ");
        stringBuilder.append(this.zzbBH);
        stringBuilder.append(", ");
        stringBuilder.append(this.zzbBI);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
