package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement;

public class zzatx extends zzauh {
    private final String zzaGr = zzKm().zzKJ();
    private final long zzbqB = zzKm().zzKu();
    private final char zzbsE;
    private final zza zzbsF;
    private final zza zzbsG;
    private final zza zzbsH;
    private final zza zzbsI;
    private final zza zzbsJ;
    private final zza zzbsK;
    private final zza zzbsL;
    private final zza zzbsM;
    private final zza zzbsN;

    public class zza {
        private final int mPriority;
        final /* synthetic */ zzatx zzbsP;
        private final boolean zzbsQ;
        private final boolean zzbsR;

        zza(zzatx com_google_android_gms_internal_zzatx, int i, boolean z, boolean z2) {
            this.zzbsP = com_google_android_gms_internal_zzatx;
            this.mPriority = i;
            this.zzbsQ = z;
            this.zzbsR = z2;
        }

        public void log(String str) {
            this.zzbsP.zza(this.mPriority, this.zzbsQ, this.zzbsR, str, null, null, null);
        }

        public void zzd(String str, Object obj, Object obj2, Object obj3) {
            this.zzbsP.zza(this.mPriority, this.zzbsQ, this.zzbsR, str, obj, obj2, obj3);
        }

        public void zze(String str, Object obj, Object obj2) {
            this.zzbsP.zza(this.mPriority, this.zzbsQ, this.zzbsR, str, obj, obj2, null);
        }

        public void zzj(String str, Object obj) {
            this.zzbsP.zza(this.mPriority, this.zzbsQ, this.zzbsR, str, obj, null, null);
        }
    }

    private static class zzb {
        private final String zzbsS;

        public zzb(@NonNull String str) {
            this.zzbsS = str;
        }
    }

    zzatx(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
        if (zzKm().zzoW()) {
            zzKm().zzLf();
            this.zzbsE = 'C';
        } else {
            zzKm().zzLf();
            this.zzbsE = 'c';
        }
        this.zzbsF = new zza(this, 6, false, false);
        this.zzbsG = new zza(this, 6, true, false);
        this.zzbsH = new zza(this, 6, false, true);
        this.zzbsI = new zza(this, 5, false, false);
        this.zzbsJ = new zza(this, 5, true, false);
        this.zzbsK = new zza(this, 5, false, true);
        this.zzbsL = new zza(this, 4, false, false);
        this.zzbsM = new zza(this, 3, false, false);
        this.zzbsN = new zza(this, 2, false, false);
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
                return valueOf instanceof zzb ? ((zzb) valueOf).zzbsS : z ? "-" : String.valueOf(valueOf);
            } else {
                Throwable th = (Throwable) valueOf;
                StringBuilder stringBuilder = new StringBuilder(z ? th.getClass().getName() : th.toString());
                String zzfF = zzfF(AppMeasurement.class.getCanonicalName());
                String zzfF2 = zzfF(zzaue.class.getCanonicalName());
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    if (!stackTraceElement.isNativeMethod()) {
                        String className = stackTraceElement.getClassName();
                        if (className != null) {
                            className = zzfF(className);
                            if (className.equals(zzfF) || className.equals(zzfF2)) {
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

    protected static Object zzfE(String str) {
        return str == null ? null : new zzb(str);
    }

    private static String zzfF(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf != -1 ? str.substring(0, lastIndexOf) : str;
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

    public zza zzLX() {
        return this.zzbsF;
    }

    public zza zzLY() {
        return this.zzbsG;
    }

    public zza zzLZ() {
        return this.zzbsI;
    }

    public zza zzMa() {
        return this.zzbsK;
    }

    public zza zzMb() {
        return this.zzbsL;
    }

    public zza zzMc() {
        return this.zzbsM;
    }

    public zza zzMd() {
        return this.zzbsN;
    }

    public String zzMe() {
        Pair zzqm = zzKl().zzbtc.zzqm();
        if (zzqm == null || zzqm == zzaua.zzbtb) {
            return null;
        }
        String valueOf = String.valueOf(String.valueOf(zzqm.second));
        String str = (String) zzqm.first;
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()).append(valueOf).append(":").append(str).toString();
    }

    protected void zza(int i, boolean z, boolean z2, String str, Object obj, Object obj2, Object obj3) {
        if (!z && zzak(i)) {
            zzn(i, zza(false, str, obj, obj2, obj3));
        }
        if (!z2 && i >= 5) {
            zzb(i, str, obj, obj2, obj3);
        }
    }

    protected boolean zzak(int i) {
        return Log.isLoggable(this.zzaGr, i);
    }

    public void zzb(int i, String str, Object obj, Object obj2, Object obj3) {
        zzac.zzw(str);
        zzaud zzMu = this.zzbqg.zzMu();
        if (zzMu == null) {
            zzn(6, "Scheduler not set. Not logging error/warn");
        } else if (zzMu.isInitialized()) {
            if (i < 0) {
                i = 0;
            }
            if (i >= "01VDIWEA?".length()) {
                i = "01VDIWEA?".length() - 1;
            }
            String valueOf = String.valueOf("2");
            char charAt = "01VDIWEA?".charAt(i);
            char c = this.zzbsE;
            long j = this.zzbqB;
            String valueOf2 = String.valueOf(zza(true, str, obj, obj2, obj3));
            valueOf = new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append(valueOf).append(charAt).append(c).append(j).append(":").append(valueOf2).toString();
            if (valueOf.length() > 1024) {
                valueOf = str.substring(0, 1024);
            }
            zzMu.zzm(new Runnable(this) {
                final /* synthetic */ zzatx zzbsP;

                public void run() {
                    zzaua zzKl = this.zzbsP.zzbqg.zzKl();
                    if (zzKl.isInitialized()) {
                        zzKl.zzbtc.zzcc(valueOf);
                    } else {
                        this.zzbsP.zzn(6, "Persisted config not initialized. Not logging error/warn");
                    }
                }
            });
        } else {
            zzn(6, "Scheduler not initialized. Not logging error/warn");
        }
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    protected void zzn(int i, String str) {
        Log.println(i, this.zzaGr, str);
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }
}
