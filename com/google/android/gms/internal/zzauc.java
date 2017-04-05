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
    private final Map<String, Map<String, String>> zzbtD = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbtE = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbtF = new ArrayMap();
    private final Map<String, zzb> zzbtG = new ArrayMap();
    private final Map<String, String> zzbtH = new ArrayMap();

    zzauc(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private Map<String, String> zza(zzb com_google_android_gms_internal_zzauv_zzb) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzauv_zzb == null || com_google_android_gms_internal_zzauv_zzb.zzbwP == null)) {
            for (zzc com_google_android_gms_internal_zzauv_zzc : com_google_android_gms_internal_zzauv_zzb.zzbwP) {
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
        if (!(com_google_android_gms_internal_zzauv_zzb == null || com_google_android_gms_internal_zzauv_zzb.zzbwQ == null)) {
            for (zza com_google_android_gms_internal_zzauv_zza : com_google_android_gms_internal_zzauv_zzb.zzbwQ) {
                if (com_google_android_gms_internal_zzauv_zza != null) {
                    String str2 = (String) AppMeasurement.zza.zzbqd.get(com_google_android_gms_internal_zzauv_zza.name);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzauv_zza.name = str2;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzauv_zza.name, com_google_android_gms_internal_zzauv_zza.zzbwL);
                    arrayMap2.put(com_google_android_gms_internal_zzauv_zza.name, com_google_android_gms_internal_zzauv_zza.zzbwM);
                }
            }
        }
        this.zzbtE.put(str, arrayMap);
        this.zzbtF.put(str, arrayMap2);
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
            zzKl().zzMe().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzauv_zzb.zzbwN, com_google_android_gms_internal_zzauv_zzb.zzbqL);
            return com_google_android_gms_internal_zzauv_zzb;
        } catch (IOException e) {
            zzKl().zzMa().zze("Unable to merge remote config. appId", zzatx.zzfE(str), e);
            return null;
        }
    }

    @WorkerThread
    private void zzfK(String str) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        if (this.zzbtG.get(str) == null) {
            byte[] zzfw = zzKg().zzfw(str);
            if (zzfw == null) {
                this.zzbtD.put(str, null);
                this.zzbtE.put(str, null);
                this.zzbtF.put(str, null);
                this.zzbtG.put(str, null);
                this.zzbtH.put(str, null);
                return;
            }
            zzb zze = zze(str, zzfw);
            this.zzbtD.put(str, zza(zze));
            zza(str, zze);
            this.zzbtG.put(str, zze);
            this.zzbtH.put(str, null);
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    @WorkerThread
    String zzZ(String str, String str2) {
        zzmR();
        zzfK(str);
        Map map = (Map) this.zzbtD.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    @WorkerThread
    boolean zzaa(String str, String str2) {
        zzmR();
        zzfK(str);
        if (zzKh().zzgg(str) && zzaut.zzgd(str2)) {
            return true;
        }
        if (zzKh().zzgh(str) && zzaut.zzfT(str2)) {
            return true;
        }
        Map map = (Map) this.zzbtE.get(str);
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
        Map map = (Map) this.zzbtF.get(str);
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
        this.zzbtG.put(str, zze);
        this.zzbtH.put(str, str2);
        this.zzbtD.put(str, zza(zze));
        zzJZ().zza(str, zze.zzbwR);
        try {
            zze.zzbwR = null;
            byte[] bArr2 = new byte[zze.zzaeT()];
            zze.zza(zzbxm.zzag(bArr2));
            bArr = bArr2;
        } catch (IOException e) {
            zzKl().zzMa().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzatx.zzfE(str), e);
        }
        zzKg().zzd(str, bArr);
        return true;
    }

    @WorkerThread
    protected zzb zzfL(String str) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzfK(str);
        return (zzb) this.zzbtG.get(str);
    }

    @WorkerThread
    protected String zzfM(String str) {
        zzmR();
        return (String) this.zzbtH.get(str);
    }

    @WorkerThread
    protected void zzfN(String str) {
        zzmR();
        this.zzbtH.put(str, null);
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
