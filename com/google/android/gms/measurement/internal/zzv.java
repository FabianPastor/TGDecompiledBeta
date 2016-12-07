package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzarc;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvl.zza;
import com.google.android.gms.internal.zzvl.zzb;
import com.google.android.gms.internal.zzvl.zzc;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.IOException;
import java.util.Map;

public class zzv extends zzaa {
    private final Map<String, Map<String, String>> aqu = new ArrayMap();
    private final Map<String, Map<String, Boolean>> aqv = new ArrayMap();
    private final Map<String, Map<String, Boolean>> aqw = new ArrayMap();
    private final Map<String, zzb> aqx = new ArrayMap();
    private final Map<String, String> aqy = new ArrayMap();

    zzv(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private Map<String, String> zza(zzb com_google_android_gms_internal_zzvl_zzb) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzvl_zzb == null || com_google_android_gms_internal_zzvl_zzb.atf == null)) {
            for (zzc com_google_android_gms_internal_zzvl_zzc : com_google_android_gms_internal_zzvl_zzb.atf) {
                if (com_google_android_gms_internal_zzvl_zzc != null) {
                    arrayMap.put(com_google_android_gms_internal_zzvl_zzc.zzcb, com_google_android_gms_internal_zzvl_zzc.value);
                }
            }
        }
        return arrayMap;
    }

    private void zza(String str, zzb com_google_android_gms_internal_zzvl_zzb) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzvl_zzb == null || com_google_android_gms_internal_zzvl_zzb.atg == null)) {
            for (zza com_google_android_gms_internal_zzvl_zza : com_google_android_gms_internal_zzvl_zzb.atg) {
                if (com_google_android_gms_internal_zzvl_zza != null) {
                    String str2 = (String) AppMeasurement.zza.anr.get(com_google_android_gms_internal_zzvl_zza.name);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzvl_zza.name = str2;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzvl_zza.name, com_google_android_gms_internal_zzvl_zza.atb);
                    arrayMap2.put(com_google_android_gms_internal_zzvl_zza.name, com_google_android_gms_internal_zzvl_zza.atc);
                }
            }
        }
        this.aqv.put(str, arrayMap);
        this.aqw.put(str, arrayMap2);
    }

    @WorkerThread
    private zzb zze(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzb();
        }
        zzarc zzbd = zzarc.zzbd(bArr);
        zzb com_google_android_gms_internal_zzvl_zzb = new zzb();
        try {
            zzb com_google_android_gms_internal_zzvl_zzb2 = (zzb) com_google_android_gms_internal_zzvl_zzb.zzb(zzbd);
            zzbvg().zzbwj().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzvl_zzb.atd, com_google_android_gms_internal_zzvl_zzb.anQ);
            return com_google_android_gms_internal_zzvl_zzb;
        } catch (IOException e) {
            zzbvg().zzbwe().zze("Unable to merge remote config", str, e);
            return null;
        }
    }

    @WorkerThread
    private void zzmo(String str) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        if (!this.aqx.containsKey(str)) {
            byte[] zzmb = zzbvb().zzmb(str);
            if (zzmb == null) {
                this.aqu.put(str, null);
                this.aqv.put(str, null);
                this.aqw.put(str, null);
                this.aqx.put(str, null);
                this.aqy.put(str, null);
                return;
            }
            zzb zze = zze(str, zzmb);
            this.aqu.put(str, zza(zze));
            zza(str, zze);
            this.aqx.put(str, zze);
            this.aqy.put(str, null);
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    @WorkerThread
    String zzaw(String str, String str2) {
        zzyl();
        zzmo(str);
        Map map = (Map) this.aqu.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    @WorkerThread
    boolean zzax(String str, String str2) {
        zzyl();
        zzmo(str);
        Map map = (Map) this.aqv.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    boolean zzay(String str, String str2) {
        zzyl();
        zzmo(str);
        Map map = (Map) this.aqw.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    protected boolean zzb(String str, byte[] bArr, String str2) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzb zze = zze(str, bArr);
        if (zze == null) {
            return false;
        }
        zza(str, zze);
        this.aqx.put(str, zze);
        this.aqy.put(str, str2);
        this.aqu.put(str, zza(zze));
        zzbuw().zza(str, zze.ath);
        try {
            zze.ath = null;
            byte[] bArr2 = new byte[zze.db()];
            zze.zza(zzard.zzbe(bArr2));
            bArr = bArr2;
        } catch (IOException e) {
            zzbvg().zzbwe().zzj("Unable to serialize reduced-size config.  Storing full config instead.", e);
        }
        zzbvb().zzd(str, bArr);
        return true;
    }

    public /* bridge */ /* synthetic */ void zzbuv() {
        super.zzbuv();
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    @WorkerThread
    protected zzb zzmp(String str) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzmo(str);
        return (zzb) this.aqx.get(str);
    }

    @WorkerThread
    protected String zzmq(String str) {
        zzyl();
        return (String) this.aqy.get(str);
    }

    @WorkerThread
    protected void zzmr(String str) {
        zzyl();
        this.aqy.put(str, null);
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }

    protected void zzym() {
    }
}
