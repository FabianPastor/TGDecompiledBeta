package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Base64;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class zzcqn extends zza {
    public static final Creator<zzcqn> CREATOR = new zzcqt();
    private static byte[][] zzazi = new byte[0][];
    private static zzcqn zzbAc = new zzcqn("", null, zzazi, zzazi, zzazi, zzazi, null, null);
    private static final zzcqs zzbAl = new zzcqo();
    private static final zzcqs zzbAm = new zzcqp();
    private static final zzcqs zzbAn = new zzcqq();
    private static final zzcqs zzbAo = new zzcqr();
    private String zzbAd;
    private byte[] zzbAe;
    private byte[][] zzbAf;
    private byte[][] zzbAg;
    private byte[][] zzbAh;
    private byte[][] zzbAi;
    private int[] zzbAj;
    private byte[][] zzbAk;

    public zzcqn(String str, byte[] bArr, byte[][] bArr2, byte[][] bArr3, byte[][] bArr4, byte[][] bArr5, int[] iArr, byte[][] bArr6) {
        this.zzbAd = str;
        this.zzbAe = bArr;
        this.zzbAf = bArr2;
        this.zzbAg = bArr3;
        this.zzbAh = bArr4;
        this.zzbAi = bArr5;
        this.zzbAj = iArr;
        this.zzbAk = bArr6;
    }

    private static void zza(StringBuilder stringBuilder, String str, int[] iArr) {
        stringBuilder.append(str);
        stringBuilder.append("=");
        if (iArr == null) {
            stringBuilder.append("null");
            return;
        }
        stringBuilder.append("(");
        int length = iArr.length;
        Object obj = 1;
        int i = 0;
        while (i < length) {
            int i2 = iArr[i];
            if (obj == null) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(i2);
            i++;
            obj = null;
        }
        stringBuilder.append(")");
    }

    private static void zza(StringBuilder stringBuilder, String str, byte[][] bArr) {
        stringBuilder.append(str);
        stringBuilder.append("=");
        if (bArr == null) {
            stringBuilder.append("null");
            return;
        }
        stringBuilder.append("(");
        int length = bArr.length;
        Object obj = 1;
        int i = 0;
        while (i < length) {
            byte[] bArr2 = bArr[i];
            if (obj == null) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("'");
            stringBuilder.append(Base64.encodeToString(bArr2, 3));
            stringBuilder.append("'");
            i++;
            obj = null;
        }
        stringBuilder.append(")");
    }

    private static List<String> zzb(byte[][] bArr) {
        if (bArr == null) {
            return Collections.emptyList();
        }
        List<String> arrayList = new ArrayList(bArr.length);
        for (byte[] encodeToString : bArr) {
            arrayList.add(Base64.encodeToString(encodeToString, 3));
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    private static List<Integer> zzc(int[] iArr) {
        if (iArr == null) {
            return Collections.emptyList();
        }
        List<Integer> arrayList = new ArrayList(iArr.length);
        for (int valueOf : iArr) {
            arrayList.add(Integer.valueOf(valueOf));
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof zzcqn)) {
            return false;
        }
        zzcqn com_google_android_gms_internal_zzcqn = (zzcqn) obj;
        return zzcqu.equals(this.zzbAd, com_google_android_gms_internal_zzcqn.zzbAd) && Arrays.equals(this.zzbAe, com_google_android_gms_internal_zzcqn.zzbAe) && zzcqu.equals(zzb(this.zzbAf), zzb(com_google_android_gms_internal_zzcqn.zzbAf)) && zzcqu.equals(zzb(this.zzbAg), zzb(com_google_android_gms_internal_zzcqn.zzbAg)) && zzcqu.equals(zzb(this.zzbAh), zzb(com_google_android_gms_internal_zzcqn.zzbAh)) && zzcqu.equals(zzb(this.zzbAi), zzb(com_google_android_gms_internal_zzcqn.zzbAi)) && zzcqu.equals(zzc(this.zzbAj), zzc(com_google_android_gms_internal_zzcqn.zzbAj)) && zzcqu.equals(zzb(this.zzbAk), zzb(com_google_android_gms_internal_zzcqn.zzbAk));
    }

    public final String toString() {
        String str;
        StringBuilder stringBuilder = new StringBuilder("ExperimentTokens");
        stringBuilder.append("(");
        if (this.zzbAd == null) {
            str = "null";
        } else {
            str = String.valueOf("'");
            String str2 = this.zzbAd;
            String valueOf = String.valueOf("'");
            str = new StringBuilder((String.valueOf(str).length() + String.valueOf(str2).length()) + String.valueOf(valueOf).length()).append(str).append(str2).append(valueOf).toString();
        }
        stringBuilder.append(str);
        stringBuilder.append(", ");
        byte[] bArr = this.zzbAe;
        stringBuilder.append("direct");
        stringBuilder.append("=");
        if (bArr == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append("'");
            stringBuilder.append(Base64.encodeToString(bArr, 3));
            stringBuilder.append("'");
        }
        stringBuilder.append(", ");
        zza(stringBuilder, "GAIA", this.zzbAf);
        stringBuilder.append(", ");
        zza(stringBuilder, "PSEUDO", this.zzbAg);
        stringBuilder.append(", ");
        zza(stringBuilder, "ALWAYS", this.zzbAh);
        stringBuilder.append(", ");
        zza(stringBuilder, "OTHER", this.zzbAi);
        stringBuilder.append(", ");
        zza(stringBuilder, "weak", this.zzbAj);
        stringBuilder.append(", ");
        zza(stringBuilder, "directs", this.zzbAk);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbAd, false);
        zzd.zza(parcel, 3, this.zzbAe, false);
        zzd.zza(parcel, 4, this.zzbAf, false);
        zzd.zza(parcel, 5, this.zzbAg, false);
        zzd.zza(parcel, 6, this.zzbAh, false);
        zzd.zza(parcel, 7, this.zzbAi, false);
        zzd.zza(parcel, 8, this.zzbAj, false);
        zzd.zza(parcel, 9, this.zzbAk, false);
        zzd.zzI(parcel, zze);
    }
}
