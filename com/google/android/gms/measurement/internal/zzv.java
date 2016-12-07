package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzars;
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwb.zza;
import com.google.android.gms.internal.zzwb.zzb;
import com.google.android.gms.internal.zzwb.zzc;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.IOException;
import java.util.Map;

public class zzv extends zzaa {
    private final Map<String, Map<String, String>> atB = new ArrayMap();
    private final Map<String, Map<String, Boolean>> atC = new ArrayMap();
    private final Map<String, Map<String, Boolean>> atD = new ArrayMap();
    private final Map<String, zzb> atE = new ArrayMap();
    private final Map<String, String> atF = new ArrayMap();

    zzv(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private Map<String, String> zza(zzb com_google_android_gms_internal_zzwb_zzb) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzwb_zzb == null || com_google_android_gms_internal_zzwb_zzb.awE == null)) {
            for (zzc com_google_android_gms_internal_zzwb_zzc : com_google_android_gms_internal_zzwb_zzb.awE) {
                if (com_google_android_gms_internal_zzwb_zzc != null) {
                    arrayMap.put(com_google_android_gms_internal_zzwb_zzc.zzcb, com_google_android_gms_internal_zzwb_zzc.value);
                }
            }
        }
        return arrayMap;
    }

    private void zza(String str, zzb com_google_android_gms_internal_zzwb_zzb) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzwb_zzb == null || com_google_android_gms_internal_zzwb_zzb.awF == null)) {
            for (zza com_google_android_gms_internal_zzwb_zza : com_google_android_gms_internal_zzwb_zzb.awF) {
                if (com_google_android_gms_internal_zzwb_zza != null) {
                    String str2 = (String) AppMeasurement.zza.aqx.get(com_google_android_gms_internal_zzwb_zza.name);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzwb_zza.name = str2;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzwb_zza.name, com_google_android_gms_internal_zzwb_zza.awA);
                    arrayMap2.put(com_google_android_gms_internal_zzwb_zza.name, com_google_android_gms_internal_zzwb_zza.awB);
                }
            }
        }
        this.atC.put(str, arrayMap);
        this.atD.put(str, arrayMap2);
    }

    @WorkerThread
    private zzb zze(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzb();
        }
        zzars zzbd = zzars.zzbd(bArr);
        zzb com_google_android_gms_internal_zzwb_zzb = new zzb();
        try {
            zzb com_google_android_gms_internal_zzwb_zzb2 = (zzb) com_google_android_gms_internal_zzwb_zzb.zzb(zzbd);
            zzbwb().zzbxe().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzwb_zzb.awC, com_google_android_gms_internal_zzwb_zzb.aqZ);
            return com_google_android_gms_internal_zzwb_zzb;
        } catch (IOException e) {
            zzbwb().zzbxa().zze("Unable to merge remote config", str, e);
            return null;
        }
    }

    @WorkerThread
    private void zzmn(String str) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        if (!this.atE.containsKey(str)) {
            byte[] zzmb = zzbvw().zzmb(str);
            if (zzmb == null) {
                this.atB.put(str, null);
                this.atC.put(str, null);
                this.atD.put(str, null);
                this.atE.put(str, null);
                this.atF.put(str, null);
                return;
            }
            zzb zze = zze(str, zzmb);
            this.atB.put(str, zza(zze));
            zza(str, zze);
            this.atE.put(str, zze);
            this.atF.put(str, null);
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    @WorkerThread
    String zzaw(String str, String str2) {
        zzzx();
        zzmn(str);
        Map map = (Map) this.atB.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    @WorkerThread
    boolean zzax(String str, String str2) {
        zzzx();
        zzmn(str);
        if (zzbvx().zznh(str) && zzal.zzne(str2)) {
            return true;
        }
        if (zzbvx().zzni(str) && zzal.zzmu(str2)) {
            return true;
        }
        Map map = (Map) this.atC.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    boolean zzay(String str, String str2) {
        zzzx();
        zzmn(str);
        Map map = (Map) this.atD.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    protected boolean zzb(String str, byte[] bArr, String str2) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzb zze = zze(str, bArr);
        if (zze == null) {
            return false;
        }
        zza(str, zze);
        this.atE.put(str, zze);
        this.atF.put(str, str2);
        this.atB.put(str, zza(zze));
        zzbvp().zza(str, zze.awG);
        try {
            zze.awG = null;
            byte[] bArr2 = new byte[zze.cz()];
            zze.zza(zzart.zzbe(bArr2));
            bArr = bArr2;
        } catch (IOException e) {
            zzbwb().zzbxa().zzj("Unable to serialize reduced-size config.  Storing full config instead.", e);
        }
        zzbvw().zzd(str, bArr);
        return true;
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    @WorkerThread
    protected zzb zzmo(String str) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzmn(str);
        return (zzb) this.atE.get(str);
    }

    @WorkerThread
    protected String zzmp(String str) {
        zzzx();
        return (String) this.atF.get(str);
    }

    @WorkerThread
    protected void zzmq(String str) {
        zzzx();
        this.atF.put(str, null);
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}
