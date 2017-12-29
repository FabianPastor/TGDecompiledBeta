package com.google.android.gms.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.io.IOException;
import java.util.Map;

public final class zzcig extends zzcjl {
    private static int zzjdx = 65535;
    private static int zzjdy = 2;
    private final Map<String, Map<String, String>> zzjdz = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzjea = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzjeb = new ArrayMap();
    private final Map<String, zzcly> zzjec = new ArrayMap();
    private final Map<String, Map<String, Integer>> zzjed = new ArrayMap();
    private final Map<String, String> zzjee = new ArrayMap();

    zzcig(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private static Map<String, String> zza(zzcly com_google_android_gms_internal_zzcly) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzcly == null || com_google_android_gms_internal_zzcly.zzjky == null)) {
            for (zzclz com_google_android_gms_internal_zzclz : com_google_android_gms_internal_zzcly.zzjky) {
                if (com_google_android_gms_internal_zzclz != null) {
                    arrayMap.put(com_google_android_gms_internal_zzclz.key, com_google_android_gms_internal_zzclz.value);
                }
            }
        }
        return arrayMap;
    }

    private final void zza(String str, zzcly com_google_android_gms_internal_zzcly) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        Map arrayMap3 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzcly == null || com_google_android_gms_internal_zzcly.zzjkz == null)) {
            for (zzclx com_google_android_gms_internal_zzclx : com_google_android_gms_internal_zzcly.zzjkz) {
                if (TextUtils.isEmpty(com_google_android_gms_internal_zzclx.name)) {
                    zzawy().zzazf().log("EventConfig contained null event name");
                } else {
                    Object zziq = Event.zziq(com_google_android_gms_internal_zzclx.name);
                    if (!TextUtils.isEmpty(zziq)) {
                        com_google_android_gms_internal_zzclx.name = zziq;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzclx.name, com_google_android_gms_internal_zzclx.zzjkt);
                    arrayMap2.put(com_google_android_gms_internal_zzclx.name, com_google_android_gms_internal_zzclx.zzjku);
                    if (com_google_android_gms_internal_zzclx.zzjkv != null) {
                        if (com_google_android_gms_internal_zzclx.zzjkv.intValue() < zzjdy || com_google_android_gms_internal_zzclx.zzjkv.intValue() > zzjdx) {
                            zzawy().zzazf().zze("Invalid sampling rate. Event name, sample rate", com_google_android_gms_internal_zzclx.name, com_google_android_gms_internal_zzclx.zzjkv);
                        } else {
                            arrayMap3.put(com_google_android_gms_internal_zzclx.name, com_google_android_gms_internal_zzclx.zzjkv);
                        }
                    }
                }
            }
        }
        this.zzjea.put(str, arrayMap);
        this.zzjeb.put(str, arrayMap2);
        this.zzjed.put(str, arrayMap3);
    }

    private final zzcly zzc(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzcly();
        }
        zzfjj zzn = zzfjj.zzn(bArr, 0, bArr.length);
        zzfjs com_google_android_gms_internal_zzcly = new zzcly();
        try {
            com_google_android_gms_internal_zzcly.zza(zzn);
            zzawy().zzazj().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzcly.zzjkw, com_google_android_gms_internal_zzcly.zzixs);
            return com_google_android_gms_internal_zzcly;
        } catch (IOException e) {
            zzawy().zzazf().zze("Unable to merge remote config. appId", zzchm.zzjk(str), e);
            return new zzcly();
        }
    }

    private final void zzjr(String str) {
        zzxf();
        zzve();
        zzbq.zzgm(str);
        if (this.zzjec.get(str) == null) {
            byte[] zzjd = zzaws().zzjd(str);
            if (zzjd == null) {
                this.zzjdz.put(str, null);
                this.zzjea.put(str, null);
                this.zzjeb.put(str, null);
                this.zzjec.put(str, null);
                this.zzjee.put(str, null);
                this.zzjed.put(str, null);
                return;
            }
            zzcly zzc = zzc(str, zzjd);
            this.zzjdz.put(str, zza(zzc));
            zza(str, zzc);
            this.zzjec.put(str, zzc);
            this.zzjee.put(str, null);
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    final String zzam(String str, String str2) {
        zzve();
        zzjr(str);
        Map map = (Map) this.zzjdz.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    final boolean zzan(String str, String str2) {
        zzve();
        zzjr(str);
        if (zzawu().zzkl(str) && zzclq.zzki(str2)) {
            return true;
        }
        if (zzawu().zzkm(str) && zzclq.zzjz(str2)) {
            return true;
        }
        Map map = (Map) this.zzjea.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    final boolean zzao(String str, String str2) {
        zzve();
        zzjr(str);
        if ("ecommerce_purchase".equals(str2)) {
            return true;
        }
        Map map = (Map) this.zzjeb.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    final int zzap(String str, String str2) {
        zzve();
        zzjr(str);
        Map map = (Map) this.zzjed.get(str);
        if (map == null) {
            return 1;
        }
        Integer num = (Integer) map.get(str2);
        return num == null ? 1 : num.intValue();
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final /* bridge */ /* synthetic */ void zzawj() {
        super.zzawj();
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    protected final boolean zzaxz() {
        return false;
    }

    protected final boolean zzb(String str, byte[] bArr, String str2) {
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzfjs zzc = zzc(str, bArr);
        if (zzc == null) {
            return false;
        }
        zza(str, zzc);
        this.zzjec.put(str, zzc);
        this.zzjee.put(str, str2);
        this.zzjdz.put(str, zza(zzc));
        zzcjk zzawl = zzawl();
        zzclr[] com_google_android_gms_internal_zzclrArr = zzc.zzjla;
        zzbq.checkNotNull(com_google_android_gms_internal_zzclrArr);
        for (zzclr com_google_android_gms_internal_zzclr : com_google_android_gms_internal_zzclrArr) {
            for (zzcls com_google_android_gms_internal_zzcls : com_google_android_gms_internal_zzclr.zzjju) {
                String zziq = Event.zziq(com_google_android_gms_internal_zzcls.zzjjx);
                if (zziq != null) {
                    com_google_android_gms_internal_zzcls.zzjjx = zziq;
                }
                for (zzclt com_google_android_gms_internal_zzclt : com_google_android_gms_internal_zzcls.zzjjy) {
                    String zziq2 = Param.zziq(com_google_android_gms_internal_zzclt.zzjkf);
                    if (zziq2 != null) {
                        com_google_android_gms_internal_zzclt.zzjkf = zziq2;
                    }
                }
            }
            for (zzclv com_google_android_gms_internal_zzclv : com_google_android_gms_internal_zzclr.zzjjt) {
                String zziq3 = UserProperty.zziq(com_google_android_gms_internal_zzclv.zzjkm);
                if (zziq3 != null) {
                    com_google_android_gms_internal_zzclv.zzjkm = zziq3;
                }
            }
        }
        zzawl.zzaws().zza(str, com_google_android_gms_internal_zzclrArr);
        try {
            zzc.zzjla = null;
            byte[] bArr2 = new byte[zzc.zzho()];
            zzc.zza(zzfjk.zzo(bArr2, 0, bArr2.length));
            bArr = bArr2;
        } catch (IOException e) {
            zzawy().zzazf().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzchm.zzjk(str), e);
        }
        zzcjk zzaws = zzaws();
        zzbq.zzgm(str);
        zzaws.zzve();
        zzaws.zzxf();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) zzaws.getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzaws.zzawy().zzazd().zzj("Failed to update remote config (got 0). appId", zzchm.zzjk(str));
            }
        } catch (SQLiteException e2) {
            zzaws.zzawy().zzazd().zze("Error storing remote config. appId", zzchm.zzjk(str), e2);
        }
        return true;
    }

    protected final zzcly zzjs(String str) {
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzjr(str);
        return (zzcly) this.zzjec.get(str);
    }

    protected final String zzjt(String str) {
        zzve();
        return (String) this.zzjee.get(str);
    }

    protected final void zzju(String str) {
        zzve();
        this.zzjee.put(str, null);
    }

    final void zzjv(String str) {
        zzve();
        this.zzjec.remove(str);
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
