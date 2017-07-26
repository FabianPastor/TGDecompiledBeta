package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.Map;

public final class zzbgj<I, O> extends zza {
    public static final zzbgm CREATOR = new zzbgm();
    protected final int zzaIH;
    protected final boolean zzaII;
    protected final int zzaIJ;
    protected final boolean zzaIK;
    protected final String zzaIL;
    protected final int zzaIM;
    protected final Class<? extends zzbgi> zzaIN;
    private String zzaIO;
    private zzbgo zzaIP;
    private zzbgk<I, O> zzaIQ;
    private final int zzaku;

    zzbgj(int i, int i2, boolean z, int i3, boolean z2, String str, int i4, String str2, zzbgc com_google_android_gms_internal_zzbgc) {
        this.zzaku = i;
        this.zzaIH = i2;
        this.zzaII = z;
        this.zzaIJ = i3;
        this.zzaIK = z2;
        this.zzaIL = str;
        this.zzaIM = i4;
        if (str2 == null) {
            this.zzaIN = null;
            this.zzaIO = null;
        } else {
            this.zzaIN = zzbgt.class;
            this.zzaIO = str2;
        }
        if (com_google_android_gms_internal_zzbgc == null) {
            this.zzaIQ = null;
        } else {
            this.zzaIQ = com_google_android_gms_internal_zzbgc.zzrK();
        }
    }

    private zzbgj(int i, boolean z, int i2, boolean z2, String str, int i3, Class<? extends zzbgi> cls, zzbgk<I, O> com_google_android_gms_internal_zzbgk_I__O) {
        this.zzaku = 1;
        this.zzaIH = i;
        this.zzaII = z;
        this.zzaIJ = i2;
        this.zzaIK = z2;
        this.zzaIL = str;
        this.zzaIM = i3;
        this.zzaIN = cls;
        if (cls == null) {
            this.zzaIO = null;
        } else {
            this.zzaIO = cls.getCanonicalName();
        }
        this.zzaIQ = com_google_android_gms_internal_zzbgk_I__O;
    }

    public static zzbgj zza(String str, int i, zzbgk<?, ?> com_google_android_gms_internal_zzbgk___, boolean z) {
        return new zzbgj(7, false, 0, false, str, i, null, com_google_android_gms_internal_zzbgk___);
    }

    public static <T extends zzbgi> zzbgj<T, T> zza(String str, int i, Class<T> cls) {
        return new zzbgj(11, false, 11, false, str, i, cls, null);
    }

    public static <T extends zzbgi> zzbgj<ArrayList<T>, ArrayList<T>> zzb(String str, int i, Class<T> cls) {
        return new zzbgj(11, true, 11, true, str, i, cls, null);
    }

    public static zzbgj<Integer, Integer> zzj(String str, int i) {
        return new zzbgj(0, false, 0, false, str, i, null, null);
    }

    public static zzbgj<Boolean, Boolean> zzk(String str, int i) {
        return new zzbgj(6, false, 6, false, str, i, null, null);
    }

    public static zzbgj<String, String> zzl(String str, int i) {
        return new zzbgj(7, false, 7, false, str, i, null, null);
    }

    private String zzrN() {
        return this.zzaIO == null ? null : this.zzaIO;
    }

    public final I convertBack(O o) {
        return this.zzaIQ.convertBack(o);
    }

    public final String toString() {
        zzbg zzg = zzbe.zzt(this).zzg("versionCode", Integer.valueOf(this.zzaku)).zzg("typeIn", Integer.valueOf(this.zzaIH)).zzg("typeInArray", Boolean.valueOf(this.zzaII)).zzg("typeOut", Integer.valueOf(this.zzaIJ)).zzg("typeOutArray", Boolean.valueOf(this.zzaIK)).zzg("outputFieldName", this.zzaIL).zzg("safeParcelFieldId", Integer.valueOf(this.zzaIM)).zzg("concreteTypeName", zzrN());
        Class cls = this.zzaIN;
        if (cls != null) {
            zzg.zzg("concreteType.class", cls.getCanonicalName());
        }
        if (this.zzaIQ != null) {
            zzg.zzg("converterName", this.zzaIQ.getClass().getCanonicalName());
        }
        return zzg.toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zzc(parcel, 2, this.zzaIH);
        zzd.zza(parcel, 3, this.zzaII);
        zzd.zzc(parcel, 4, this.zzaIJ);
        zzd.zza(parcel, 5, this.zzaIK);
        zzd.zza(parcel, 6, this.zzaIL, false);
        zzd.zzc(parcel, 7, this.zzaIM);
        zzd.zza(parcel, 8, zzrN(), false);
        zzd.zza(parcel, 9, this.zzaIQ == null ? null : zzbgc.zza(this.zzaIQ), i, false);
        zzd.zzI(parcel, zze);
    }

    public final void zza(zzbgo com_google_android_gms_internal_zzbgo) {
        this.zzaIP = com_google_android_gms_internal_zzbgo;
    }

    public final int zzrM() {
        return this.zzaIM;
    }

    public final boolean zzrO() {
        return this.zzaIQ != null;
    }

    public final Map<String, zzbgj<?, ?>> zzrP() {
        zzbo.zzu(this.zzaIO);
        zzbo.zzu(this.zzaIP);
        return this.zzaIP.zzcJ(this.zzaIO);
    }
}
