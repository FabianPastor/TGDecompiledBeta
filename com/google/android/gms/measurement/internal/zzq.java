package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement;

public class zzq extends zzaa {
    private final String EC = zzbwd().zzbub();
    private final long aqN = zzbwd().zzbto();
    private final char asA;
    private final zza asB;
    private final zza asC;
    private final zza asD;
    private final zza asE;
    private final zza asF;
    private final zza asG;
    private final zza asH;
    private final zza asI;
    private final zza asJ;

    public class zza {
        final /* synthetic */ zzq asL;
        private final boolean asM;
        private final boolean asN;
        private final int mPriority;

        zza(zzq com_google_android_gms_measurement_internal_zzq, int i, boolean z, boolean z2) {
            this.asL = com_google_android_gms_measurement_internal_zzq;
            this.mPriority = i;
            this.asM = z;
            this.asN = z2;
        }

        public void log(String str) {
            this.asL.zza(this.mPriority, this.asM, this.asN, str, null, null, null);
        }

        public void zzd(String str, Object obj, Object obj2, Object obj3) {
            this.asL.zza(this.mPriority, this.asM, this.asN, str, obj, obj2, obj3);
        }

        public void zze(String str, Object obj, Object obj2) {
            this.asL.zza(this.mPriority, this.asM, this.asN, str, obj, obj2, null);
        }

        public void zzj(String str, Object obj) {
            this.asL.zza(this.mPriority, this.asM, this.asN, str, obj, null, null);
        }
    }

    zzq(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
        if (zzbwd().zzaef()) {
            zzbwd().zzayi();
            this.asA = 'C';
        } else {
            zzbwd().zzayi();
            this.asA = 'c';
        }
        this.asB = new zza(this, 6, false, false);
        this.asC = new zza(this, 6, true, false);
        this.asD = new zza(this, 6, false, true);
        this.asE = new zza(this, 5, false, false);
        this.asF = new zza(this, 5, true, false);
        this.asG = new zza(this, 5, false, true);
        this.asH = new zza(this, 4, false, false);
        this.asI = new zza(this, 3, false, false);
        this.asJ = new zza(this, 2, false, false);
    }

    static String zza(boolean z, String str, Object obj, Object obj2, Object obj3) {
        if (str == null) {
            Object obj4 = "";
        }
        Object zzc = zzc(z, obj);
        Object zzc2 = zzc(z, obj2);
        Object zzc3 = zzc(z, obj3);
        StringBuilder stringBuilder = new StringBuilder();
        String str2 = "";
        if (!TextUtils.isEmpty(obj4)) {
            stringBuilder.append(obj4);
            str2 = ": ";
        }
        if (!TextUtils.isEmpty(zzc)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzc);
            str2 = ", ";
        }
        if (!TextUtils.isEmpty(zzc2)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzc2);
            str2 = ", ";
        }
        if (!TextUtils.isEmpty(zzc3)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzc3);
        }
        return stringBuilder.toString();
    }

    static String zzc(boolean z, Object obj) {
        if (obj == null) {
            return "";
        }
        Object valueOf = obj instanceof Integer ? Long.valueOf((long) ((Integer) obj).intValue()) : obj;
        if (valueOf instanceof Long) {
            if (!z) {
                return String.valueOf(valueOf);
            }
            if (Math.abs(((Long) valueOf).longValue()) < 100) {
                return String.valueOf(valueOf);
            }
            String str = String.valueOf(valueOf).charAt(0) == '-' ? "-" : "";
            String valueOf2 = String.valueOf(Math.abs(((Long) valueOf).longValue()));
            return new StringBuilder((String.valueOf(str).length() + 43) + String.valueOf(str).length()).append(str).append(Math.round(Math.pow(10.0d, (double) (valueOf2.length() - 1)))).append("...").append(str).append(Math.round(Math.pow(10.0d, (double) valueOf2.length()) - 1.0d)).toString();
        } else if (valueOf instanceof Boolean) {
            return String.valueOf(valueOf);
        } else {
            if (!(valueOf instanceof Throwable)) {
                return z ? "-" : String.valueOf(valueOf);
            } else {
                Throwable th = (Throwable) valueOf;
                StringBuilder stringBuilder = new StringBuilder(z ? th.getClass().getName() : th.toString());
                String zzmj = zzmj(AppMeasurement.class.getCanonicalName());
                String zzmj2 = zzmj(zzx.class.getCanonicalName());
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    if (!stackTraceElement.isNativeMethod()) {
                        String className = stackTraceElement.getClassName();
                        if (className != null) {
                            className = zzmj(className);
                            if (className.equals(zzmj) || className.equals(zzmj2)) {
                                stringBuilder.append(": ");
                                stringBuilder.append(stackTraceElement);
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }
                return stringBuilder.toString();
            }
        }
    }

    private static String zzmj(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf != -1 ? str.substring(0, lastIndexOf) : str;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    protected void zza(int i, boolean z, boolean z2, String str, Object obj, Object obj2, Object obj3) {
        if (!z && zzbi(i)) {
            zzn(i, zza(false, str, obj, obj2, obj3));
        }
        if (!z2 && i >= 5) {
            zzb(i, str, obj, obj2, obj3);
        }
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    public void zzb(int i, String str, Object obj, Object obj2, Object obj3) {
        zzaa.zzy(str);
        zzw zzbxs = this.aqw.zzbxs();
        if (zzbxs == null) {
            zzn(6, "Scheduler not set. Not logging error/warn.");
        } else if (!zzbxs.isInitialized()) {
            zzn(6, "Scheduler not initialized. Not logging error/warn.");
        } else if (zzbxs.zzbyn()) {
            zzn(6, "Scheduler shutdown. Not logging error/warn.");
        } else {
            if (i < 0) {
                i = 0;
            }
            if (i >= "01VDIWEA?".length()) {
                i = "01VDIWEA?".length() - 1;
            }
            String valueOf = String.valueOf("1");
            char charAt = "01VDIWEA?".charAt(i);
            char c = this.asA;
            long j = this.aqN;
            String valueOf2 = String.valueOf(zza(true, str, obj, obj2, obj3));
            valueOf = new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append(valueOf).append(charAt).append(c).append(j).append(":").append(valueOf2).toString();
            if (valueOf.length() > 1024) {
                valueOf = str.substring(0, 1024);
            }
            zzbxs.zzm(new Runnable(this) {
                final /* synthetic */ zzq asL;

                public void run() {
                    zzt zzbwc = this.asL.aqw.zzbwc();
                    if (!zzbwc.isInitialized() || zzbwc.zzbyn()) {
                        this.asL.zzn(6, "Persisted config not initialized . Not logging error/warn.");
                    } else {
                        zzbwc.asY.zzfg(valueOf);
                    }
                }
            });
        }
    }

    protected boolean zzbi(int i) {
        return Log.isLoggable(this.EC, i);
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

    public zza zzbwy() {
        return this.asB;
    }

    public zza zzbwz() {
        return this.asC;
    }

    public zza zzbxa() {
        return this.asE;
    }

    public zza zzbxb() {
        return this.asG;
    }

    public zza zzbxc() {
        return this.asH;
    }

    public zza zzbxd() {
        return this.asI;
    }

    public zza zzbxe() {
        return this.asJ;
    }

    public String zzbxf() {
        Pair zzagw = zzbwc().asY.zzagw();
        if (zzagw == null || zzagw == zzt.asX) {
            return null;
        }
        String valueOf = String.valueOf(String.valueOf(zzagw.second));
        String str = (String) zzagw.first;
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()).append(valueOf).append(":").append(str).toString();
    }

    protected void zzn(int i, String str) {
        Log.println(i, this.EC, str);
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}
