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

public final class zzcgf extends zzchj {
    private final Map<String, Map<String, String>> zzbrQ = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbrR = new ArrayMap();
    private final Map<String, Map<String, Boolean>> zzbrS = new ArrayMap();
    private final Map<String, zzcjt> zzbrT = new ArrayMap();
    private final Map<String, String> zzbrU = new ArrayMap();

    zzcgf(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
    }

    private static Map<String, String> zza(zzcjt com_google_android_gms_internal_zzcjt) {
        Map<String, String> arrayMap = new ArrayMap();
        if (!(com_google_android_gms_internal_zzcjt == null || com_google_android_gms_internal_zzcjt.zzbvn == null)) {
            for (zzcju com_google_android_gms_internal_zzcju : com_google_android_gms_internal_zzcjt.zzbvn) {
                if (com_google_android_gms_internal_zzcju != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcju.key, com_google_android_gms_internal_zzcju.value);
                }
            }
        }
        return arrayMap;
    }

    private final void zza(String str, zzcjt com_google_android_gms_internal_zzcjt) {
        Map arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        if (!(com_google_android_gms_internal_zzcjt == null || com_google_android_gms_internal_zzcjt.zzbvo == null)) {
            for (zzcjs com_google_android_gms_internal_zzcjs : com_google_android_gms_internal_zzcjt.zzbvo) {
                if (com_google_android_gms_internal_zzcjs != null) {
                    String zzdF = Event.zzdF(com_google_android_gms_internal_zzcjs.name);
                    if (zzdF != null) {
                        com_google_android_gms_internal_zzcjs.name = zzdF;
                    }
                    arrayMap.put(com_google_android_gms_internal_zzcjs.name, com_google_android_gms_internal_zzcjs.zzbvj);
                    arrayMap2.put(com_google_android_gms_internal_zzcjs.name, com_google_android_gms_internal_zzcjs.zzbvk);
                }
            }
        }
        this.zzbrR.put(str, arrayMap);
        this.zzbrS.put(str, arrayMap2);
    }

    @WorkerThread
    private final zzcjt zzc(String str, byte[] bArr) {
        if (bArr == null) {
            return new zzcjt();
        }
        adg zzb = adg.zzb(bArr, 0, bArr.length);
        zzcjt com_google_android_gms_internal_zzcjt = new zzcjt();
        try {
            com_google_android_gms_internal_zzcjt.zza(zzb);
            super.zzwF().zzyD().zze("Parsed config. version, gmp_app_id", com_google_android_gms_internal_zzcjt.zzbvl, com_google_android_gms_internal_zzcjt.zzboQ);
            return com_google_android_gms_internal_zzcjt;
        } catch (IOException e) {
            super.zzwF().zzyz().zze("Unable to merge remote config. appId", zzcfl.zzdZ(str), e);
            return new zzcjt();
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
            zzcjt zzc = zzc(str, zzdS);
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
        if (super.zzwB().zzeA(str) && zzcjl.zzex(str2)) {
            return true;
        }
        if (super.zzwB().zzeB(str) && zzcjl.zzeo(str2)) {
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
        zzcjt zzc = zzc(str, bArr);
        if (zzc == null) {
            return false;
        }
        zza(str, zzc);
        this.zzbrT.put(str, zzc);
        this.zzbrU.put(str, str2);
        this.zzbrQ.put(str, zza(zzc));
        zzcej zzws = super.zzws();
        zzcjm[] com_google_android_gms_internal_zzcjmArr = zzc.zzbvp;
        zzbo.zzu(com_google_android_gms_internal_zzcjmArr);
        for (zzcjm com_google_android_gms_internal_zzcjm : com_google_android_gms_internal_zzcjmArr) {
            for (zzcjn com_google_android_gms_internal_zzcjn : com_google_android_gms_internal_zzcjm.zzbuK) {
                String zzdF = Event.zzdF(com_google_android_gms_internal_zzcjn.zzbuN);
                if (zzdF != null) {
                    com_google_android_gms_internal_zzcjn.zzbuN = zzdF;
                }
                for (zzcjo com_google_android_gms_internal_zzcjo : com_google_android_gms_internal_zzcjn.zzbuO) {
                    String zzdF2 = Param.zzdF(com_google_android_gms_internal_zzcjo.zzbuV);
                    if (zzdF2 != null) {
                        com_google_android_gms_internal_zzcjo.zzbuV = zzdF2;
                    }
                }
            }
            for (zzcjq com_google_android_gms_internal_zzcjq : com_google_android_gms_internal_zzcjm.zzbuJ) {
                String zzdF3 = UserProperty.zzdF(com_google_android_gms_internal_zzcjq.zzbvc);
                if (zzdF3 != null) {
                    com_google_android_gms_internal_zzcjq.zzbvc = zzdF3;
                }
            }
        }
        zzws.zzwz().zza(str, com_google_android_gms_internal_zzcjmArr);
        try {
            zzc.zzbvp = null;
            byte[] bArr2 = new byte[zzc.zzLV()];
            zzc.zza(adh.zzc(bArr2, 0, bArr2.length));
            bArr = bArr2;
        } catch (IOException e) {
            super.zzwF().zzyz().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzcfl.zzdZ(str), e);
        }
        zzcen zzwz = super.zzwz();
        zzbo.zzcF(str);
        zzwz.zzjC();
        zzwz.zzkD();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) zzwz.getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzwz.zzwF().zzyx().zzj("Failed to update remote config (got 0). appId", zzcfl.zzdZ(str));
            }
        } catch (SQLiteException e2) {
            zzwz.zzwF().zzyx().zze("Error storing remote config. appId", zzcfl.zzdZ(str), e2);
        }
        return true;
    }

    @WorkerThread
    protected final zzcjt zzeh(String str) {
        zzkD();
        super.zzjC();
        zzbo.zzcF(str);
        zzeg(str);
        return (zzcjt) this.zzbrT.get(str);
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

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
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

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }
}
