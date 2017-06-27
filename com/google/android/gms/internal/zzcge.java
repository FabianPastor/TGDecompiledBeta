package com.google.android.gms.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.IOException;
import java.util.Map;

public final class zzcge extends zzchi {
    private final Map<String, Map<String, String>> zzbrQ = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbrR = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbrS = new ArrayMap();
    private final Map<String, zzcjs> zzbrT = new ArrayMap();
    private final Map<String, String> zzbrU = new ArrayMap();

    zzcge(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    private static Map<String, String> zza(zzcjs com_google_android_gms_internal_zzcjs) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzcjs == null || com_google_android_gms_internal_zzcjs.zzbvn == null)) {
            for (zzcjt com_google_android_gms_internal_zzcjt : com_google_android_gms_internal_zzcjs.zzbvn) {
                if (com_google_android_gms_internal_zzcjt != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjt.key, com_google_android_gms_internal_zzcjt.value);
                }
            }
        }
        return arrayMap;
    }

    private final void zza(String str, zzcjs com_google_android_gms_internal_zzcjs) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzcjs == null || com_google_android_gms_internal_zzcjs.zzbvo == null)) {
            for (zzcjr com_google_android_gms_internal_zzcjr : com_google_android_gms_internal_zzcjs.zzbvo) {
                if (com_google_android_gms_internal_zzcjr != null) {
                    String zzdF = Event.zzdF(com_google_android_gms_internal_zzcjr.name);
                    if (zzdF != null) {
                        com_google_android_gms_internal_zzcjr.name = zzdF;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzcjr.name, com_google_android_gms_internal_zzcjr.zzbvj);
                    arrayMap2.put(com_google_android_gms_internal_zzcjr.name, com_google_android_gms_internal_zzcjr.zzbvk);
                }
            }
        }
        this.zzbrR.put(str, arrayMap);
        this.zzbrS.put(str, arrayMap2);
    }

    @WorkerThread
    private final zzcjs zzc(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzcjs();
        }
        acx zzb = acx.zzb(bArr, 0, bArr.length);
        zzcjs com_google_android_gms_internal_zzcjs = new zzcjs();
        try {
            com_google_android_gms_internal_zzcjs.zza(zzb);
            super.zzwF().zzyD().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzcjs.zzbvl, com_google_android_gms_internal_zzcjs.zzboQ);
            return com_google_android_gms_internal_zzcjs;
        } catch (IOException e) {
            super.zzwF().zzyz().zze("Unable to merge remote config. appId", zzcfk.zzdZ(str), e);
            return new zzcjs();
        }
    }

    @WorkerThread
    private final void zzeg(String str) {
        zzkD();
        super.zzjC();
        zzbo.zzcF(str);
        if (this.zzbrT.get(str) == null) {
            byte[] zzdS = super.zzwz().zzdS(str);
            if (zzdS == null) {
                this.zzbrQ.put(str, null);
                this.zzbrR.put(str, null);
                this.zzbrS.put(str, null);
                this.zzbrT.put(str, null);
                this.zzbrU.put(str, null);
                return;
            }
            zzcjs zzc = zzc(str, zzdS);
            this.zzbrQ.put(str, zza(zzc));
            zza(str, zzc);
            this.zzbrT.put(str, zzc);
            this.zzbrU.put(str, null);
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    final String zzM(String str, String str2) {
        super.zzjC();
        zzeg(str);
        Map map = (Map) this.zzbrQ.get(str);
        return map != null ? (String) map.get(str2) : null;
    }

    @WorkerThread
    final boolean zzN(String str, String str2) {
        super.zzjC();
        zzeg(str);
        if (super.zzwB().zzeA(str) && zzcjk.zzex(str2)) {
            return true;
        }
        if (super.zzwB().zzeB(str) && zzcjk.zzeo(str2)) {
            return true;
        }
        Map map = (Map) this.zzbrR.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    final boolean zzO(String str, String str2) {
        super.zzjC();
        zzeg(str);
        if (FirebaseAnalytics.Event.ECOMMERCE_PURCHASE.equals(str2)) {
            return true;
        }
        Map map = (Map) this.zzbrS.get(str);
        if (map == null) {
            return false;
        }
        Boolean bool = (Boolean) map.get(str2);
        return bool == null ? false : bool.booleanValue();
    }

    @WorkerThread
    protected final boolean zzb(String str, byte[] bArr, String str2) {
        zzkD();
        super.zzjC();
        zzbo.zzcF(str);
        zzcjs zzc = zzc(str, bArr);
        if (zzc == null) {
            return false;
        }
        zza(str, zzc);
        this.zzbrT.put(str, zzc);
        this.zzbrU.put(str, str2);
        this.zzbrQ.put(str, zza(zzc));
        zzcei zzws = super.zzws();
        zzcjl[] com_google_android_gms_internal_zzcjlArr = zzc.zzbvp;
        zzbo.zzu(com_google_android_gms_internal_zzcjlArr);
        for (zzcjl com_google_android_gms_internal_zzcjl : com_google_android_gms_internal_zzcjlArr) {
            for (zzcjm com_google_android_gms_internal_zzcjm : com_google_android_gms_internal_zzcjl.zzbuK) {
                String zzdF = Event.zzdF(com_google_android_gms_internal_zzcjm.zzbuN);
                if (zzdF != null) {
                    com_google_android_gms_internal_zzcjm.zzbuN = zzdF;
                }
                for (zzcjn com_google_android_gms_internal_zzcjn : com_google_android_gms_internal_zzcjm.zzbuO) {
                    String zzdF2 = Param.zzdF(com_google_android_gms_internal_zzcjn.zzbuV);
                    if (zzdF2 != null) {
                        com_google_android_gms_internal_zzcjn.zzbuV = zzdF2;
                    }
                }
            }
            for (zzcjp com_google_android_gms_internal_zzcjp : com_google_android_gms_internal_zzcjl.zzbuJ) {
                String zzdF3 = UserProperty.zzdF(com_google_android_gms_internal_zzcjp.zzbvc);
                if (zzdF3 != null) {
                    com_google_android_gms_internal_zzcjp.zzbvc = zzdF3;
                }
            }
        }
        zzws.zzwz().zza(str, com_google_android_gms_internal_zzcjlArr);
        try {
            zzc.zzbvp = null;
            byte[] bArr2 = new byte[zzc.zzLT()];
            zzc.zza(acy.zzc(bArr2, 0, bArr2.length));
            bArr = bArr2;
        } catch (IOException e) {
            super.zzwF().zzyz().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzcfk.zzdZ(str), e);
        }
        zzcem zzwz = super.zzwz();
        zzbo.zzcF(str);
        zzwz.zzjC();
        zzwz.zzkD();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) zzwz.getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzwz.zzwF().zzyx().zzj("Failed to update remote config (got 0). appId", zzcfk.zzdZ(str));
            }
        } catch (SQLiteException e2) {
            zzwz.zzwF().zzyx().zze("Error storing remote config. appId", zzcfk.zzdZ(str), e2);
        }
        return true;
    }

    @WorkerThread
    protected final zzcjs zzeh(String str) {
        zzkD();
        super.zzjC();
        zzbo.zzcF(str);
        zzeg(str);
        return (zzcjs) this.zzbrT.get(str);
    }

    @WorkerThread
    protected final String zzei(String str) {
        super.zzjC();
        return (String) this.zzbrU.get(str);
    }

    @WorkerThread
    protected final void zzej(String str) {
        super.zzjC();
        this.zzbrU.put(str, null);
    }

    @WorkerThread
    final void zzek(String str) {
        super.zzjC();
        this.zzbrT.remove(str);
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }
}