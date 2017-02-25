package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzauv.zza;
import com.google.android.gms.internal.zzauv.zzb;
import com.google.android.gms.internal.zzauv.zzc;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.IOException;
import java.util.Map;

public class zzauc extends zzauh {
    private final Map<String, Map<String, String>> zzbtH = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbtI = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbtJ = new ArrayMap();
    private final Map<String, zzb> zzbtK = new ArrayMap();
    private final Map<String, String> zzbtL = new ArrayMap();

    zzauc(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private Map<String, String> zza(zzb com_google_android_gms_internal_zzauv_zzb) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzauv_zzb == null || com_google_android_gms_internal_zzauv_zzb.zzbwT == null)) {
            for (zzc com_google_android_gms_internal_zzauv_zzc : com_google_android_gms_internal_zzauv_zzb.zzbwT) {
                if (com_google_android_gms_internal_zzauv_zzc != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauv_zzc.zzaB, com_google_android_gms_internal_zzauv_zzc.value);
                }
            }
        }
        return arrayMap;
    }

    private void zza(String str, zzb com_google_android_gms_internal_zzauv_zzb) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzauv_zzb == null || com_google_android_gms_internal_zzauv_zzb.zzbwU == null)) {
            for (zza com_google_android_gms_internal_zzauv_zza : com_google_android_gms_internal_zzauv_zzb.zzbwU) {
                if (com_google_android_gms_internal_zzauv_zza != null) {
                    String str2 = (String) AppMeasurement.zza.zzbqh.get(com_google_android_gms_internal_zzauv_zza.name);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzauv_zza.name = str2;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzauv_zza.name, com_google_android_gms_internal_zzauv_zza.zzbwP);
                    arrayMap2.put(com_google_android_gms_internal_zzauv_zza.name, com_google_android_gms_internal_zzauv_zza.zzbwQ);
                }
            }
        }
        this.zzbtI.put(str, arrayMap);
        this.zzbtJ.put(str, arrayMap2);
    }

    @WorkerThread
    private zzb zze(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzb();
        }
        zzbxl zzaf = zzbxl.zzaf(bArr);
        zzb com_google_android_gms_internal_zzauv_zzb = new zzb();
        try {
            com_google_android_gms_internal_zzauv_zzb.zzb(zzaf);
            zzKk().zzMd().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzauv_zzb.zzbwR, com_google_android_gms_internal_zzauv_zzb.zzbqP);
            return com_google_android_gms_internal_zzauv_zzb;
        } catch (IOException e) {
            zzKk().zzLZ().zze("Unable to merge remote config. appId", zzatx.zzfE(str), e);
            return null;
        }
    }

    @WorkerThread
    private void zzfK(String str) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        if (this.zzbtK.get(str) == null) {
            byte[] zzfw = zzKf().zzfw(str);
            if (zzfw == null) {
                this.zzbtH.put(str, null);
                this.zzbtI.put(str, null);
                this.zzbtJ.put(str, null);
                this.zzbtK.put(str, null);
                this.zzbtL.put(str, null);
                return;
            }
            zzb zze = zze(str, zzfw);
            this.zzbtH.put(str, zza(zze));
            zza(str, zze);
            this.zzbtK.put(str, zze);
            this.zzbtL.put(str, null);
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJU() {
        super.zzJU();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ zzatb zzJX() {
        return super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatf zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzauj zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzatu zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatl zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzaul zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzauk zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzatv zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatj zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzaut zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzauc zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzaun zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaud zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzatx zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzaua zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzati zzKm() {
        return super.zzKm();
    }

    @WorkerThread
    String zzZ(String str, String str2) {
        zzmR();
        zzfK(str);
        Map map = (Map) this.zzbtH.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    @WorkerThread
    boolean zzaa(String str, String str2) {
        zzmR();
        zzfK(str);
        if (zzKg().zzgg(str) && zzaut.zzgd(str2)) {
            return true;
        }
        if (zzKg().zzgh(str) && zzaut.zzfT(str2)) {
            return true;
        }
        Map map = (Map) this.zzbtI.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    boolean zzab(String str, String str2) {
        zzmR();
        zzfK(str);
        Map map = (Map) this.zzbtJ.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    protected boolean zzb(String str, byte[] bArr, String str2) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzb zze = zze(str, bArr);
        if (zze == null) {
            return false;
        }
        zza(str, zze);
        this.zzbtK.put(str, zze);
        this.zzbtL.put(str, str2);
        this.zzbtH.put(str, zza(zze));
        zzJY().zza(str, zze.zzbwV);
        try {
            zze.zzbwV = null;
            byte[] bArr2 = new byte[zze.zzaeS()];
            zze.zza(zzbxm.zzag(bArr2));
            bArr = bArr2;
        } catch (IOException e) {
            zzKk().zzLZ().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzatx.zzfE(str), e);
        }
        zzKf().zzd(str, bArr);
        return true;
    }

    @WorkerThread
    protected zzb zzfL(String str) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzfK(str);
        return (zzb) this.zzbtK.get(str);
    }

    @WorkerThread
    protected String zzfM(String str) {
        zzmR();
        return (String) this.zzbtL.get(str);
    }

    @WorkerThread
    protected void zzfN(String str) {
        zzmR();
        this.zzbtL.put(str, null);
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }
}
