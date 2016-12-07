package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzaug.zza;
import com.google.android.gms.internal.zzaug.zzb;
import com.google.android.gms.internal.zzaug.zzc;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.IOException;
import java.util.Map;

public class zzatn extends zzats {
    private final Map<String, Map<String, String>> zzbsH = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbsI = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbsJ = new ArrayMap();
    private final Map<String, zzb> zzbsK = new ArrayMap();
    private final Map<String, String> zzbsL = new ArrayMap();

    zzatn(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    private Map<String, String> zza(zzb com_google_android_gms_internal_zzaug_zzb) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzaug_zzb == null || com_google_android_gms_internal_zzaug_zzb.zzbvM == null)) {
            for (zzc com_google_android_gms_internal_zzaug_zzc : com_google_android_gms_internal_zzaug_zzb.zzbvM) {
                if (com_google_android_gms_internal_zzaug_zzc != null) {
                    arrayMap.put(com_google_android_gms_internal_zzaug_zzc.zzaA, com_google_android_gms_internal_zzaug_zzc.value);
                }
            }
        }
        return arrayMap;
    }

    private void zza(String str, zzb com_google_android_gms_internal_zzaug_zzb) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzaug_zzb == null || com_google_android_gms_internal_zzaug_zzb.zzbvN == null)) {
            for (zza com_google_android_gms_internal_zzaug_zza : com_google_android_gms_internal_zzaug_zzb.zzbvN) {
                if (com_google_android_gms_internal_zzaug_zza != null) {
                    String str2 = (String) AppMeasurement.zza.zzbpx.get(com_google_android_gms_internal_zzaug_zza.name);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzaug_zza.name = str2;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzaug_zza.name, com_google_android_gms_internal_zzaug_zza.zzbvI);
                    arrayMap2.put(com_google_android_gms_internal_zzaug_zza.name, com_google_android_gms_internal_zzaug_zza.zzbvJ);
                }
            }
        }
        this.zzbsI.put(str, arrayMap);
        this.zzbsJ.put(str, arrayMap2);
    }

    @WorkerThread
    private zzb zze(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzb();
        }
        zzbul zzad = zzbul.zzad(bArr);
        zzb com_google_android_gms_internal_zzaug_zzb = new zzb();
        try {
            com_google_android_gms_internal_zzaug_zzb.zzb(zzad);
            zzJt().zzLg().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzaug_zzb.zzbvK, com_google_android_gms_internal_zzaug_zzb.zzbqf);
            return com_google_android_gms_internal_zzaug_zzb;
        } catch (IOException e) {
            zzJt().zzLc().zze("Unable to merge remote config. appId", zzati.zzfI(str), e);
            return null;
        }
    }

    @WorkerThread
    private void zzfN(String str) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        if (!this.zzbsK.containsKey(str)) {
            byte[] zzfA = zzJo().zzfA(str);
            if (zzfA == null) {
                this.zzbsH.put(str, null);
                this.zzbsI.put(str, null);
                this.zzbsJ.put(str, null);
                this.zzbsK.put(str, null);
                this.zzbsL.put(str, null);
                return;
            }
            zzb zze = zze(str, zzfA);
            this.zzbsH.put(str, zza(zze));
            zza(str, zze);
            this.zzbsK.put(str, zze);
            this.zzbsL.put(str, null);
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    @WorkerThread
    String zzW(String str, String str2) {
        zzmq();
        zzfN(str);
        Map map = (Map) this.zzbsH.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    @WorkerThread
    boolean zzX(String str, String str2) {
        zzmq();
        zzfN(str);
        if (zzJp().zzgj(str) && zzaue.zzgg(str2)) {
            return true;
        }
        if (zzJp().zzgk(str) && zzaue.zzfW(str2)) {
            return true;
        }
        Map map = (Map) this.zzbsI.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    boolean zzY(String str, String str2) {
        zzmq();
        zzfN(str);
        Map map = (Map) this.zzbsJ.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    protected boolean zzb(String str, byte[] bArr, String str2) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzb zze = zze(str, bArr);
        if (zze == null) {
            return false;
        }
        zza(str, zze);
        this.zzbsK.put(str, zze);
        this.zzbsL.put(str, str2);
        this.zzbsH.put(str, zza(zze));
        zzJh().zza(str, zze.zzbvO);
        try {
            zze.zzbvO = null;
            byte[] bArr2 = new byte[zze.zzacZ()];
            zze.zza(zzbum.zzae(bArr2));
            bArr = bArr2;
        } catch (IOException e) {
            zzJt().zzLc().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzati.zzfI(str), e);
        }
        zzJo().zzd(str, bArr);
        return true;
    }

    @WorkerThread
    protected zzb zzfO(String str) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzfN(str);
        return (zzb) this.zzbsK.get(str);
    }

    @WorkerThread
    protected String zzfP(String str) {
        zzmq();
        return (String) this.zzbsL.get(str);
    }

    @WorkerThread
    protected void zzfQ(String str) {
        zzmq();
        this.zzbsL.put(str, null);
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
