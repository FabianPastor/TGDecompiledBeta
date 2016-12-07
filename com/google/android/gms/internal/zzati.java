package com.google.android.gms.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement;

public class zzati extends zzats {
    private final String zzaEU = zzJv().zzJS();
    private final long zzbpS = zzJv().zzJD();
    private final char zzbrG;
    private final zza zzbrH;
    private final zza zzbrI;
    private final zza zzbrJ;
    private final zza zzbrK;
    private final zza zzbrL;
    private final zza zzbrM;
    private final zza zzbrN;
    private final zza zzbrO;
    private final zza zzbrP;

    public class zza {
        private final int mPriority;
        final /* synthetic */ zzati zzbrR;
        private final boolean zzbrS;
        private final boolean zzbrT;

        zza(zzati com_google_android_gms_internal_zzati, int i, boolean z, boolean z2) {
            this.zzbrR = com_google_android_gms_internal_zzati;
            this.mPriority = i;
            this.zzbrS = z;
            this.zzbrT = z2;
        }

        public void log(String str) {
            this.zzbrR.zza(this.mPriority, this.zzbrS, this.zzbrT, str, null, null, null);
        }

        public void zzd(String str, Object obj, Object obj2, Object obj3) {
            this.zzbrR.zza(this.mPriority, this.zzbrS, this.zzbrT, str, obj, obj2, obj3);
        }

        public void zze(String str, Object obj, Object obj2) {
            this.zzbrR.zza(this.mPriority, this.zzbrS, this.zzbrT, str, obj, obj2, null);
        }

        public void zzj(String str, Object obj) {
            this.zzbrR.zza(this.mPriority, this.zzbrS, this.zzbrT, str, obj, null, null);
        }
    }

    private static class zzb {
        private final String zzbrU;

        public zzb(@NonNull String str) {
            this.zzbrU = str;
        }
    }

    zzati(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
        if (zzJv().zzow()) {
            zzJv().zzKk();
            this.zzbrG = 'C';
        } else {
            zzJv().zzKk();
            this.zzbrG = 'c';
        }
        this.zzbrH = new zza(this, 6, false, false);
        this.zzbrI = new zza(this, 6, true, false);
        this.zzbrJ = new zza(this, 6, false, true);
        this.zzbrK = new zza(this, 5, false, false);
        this.zzbrL = new zza(this, 5, true, false);
        this.zzbrM = new zza(this, 5, false, true);
        this.zzbrN = new zza(this, 4, false, false);
        this.zzbrO = new zza(this, 3, false, false);
        this.zzbrP = new zza(this, 2, false, false);
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
                return valueOf instanceof zzb ? ((zzb) valueOf).zzbrU : z ? "-" : String.valueOf(valueOf);
            } else {
                Throwable th = (Throwable) valueOf;
                StringBuilder stringBuilder = new StringBuilder(z ? th.getClass().getName() : th.toString());
                String zzfJ = zzfJ(AppMeasurement.class.getCanonicalName());
                String zzfJ2 = zzfJ(zzatp.class.getCanonicalName());
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    if (!stackTraceElement.isNativeMethod()) {
                        String className = stackTraceElement.getClassName();
                        if (className != null) {
                            className = zzfJ(className);
                            if (className.equals(zzfJ) || className.equals(zzfJ2)) {
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

    protected static Object zzfI(String str) {
        return str == null ? null : new zzb(str);
    }

    private static String zzfJ(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf != -1 ? str.substring(0, lastIndexOf) : str;
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

    public zza zzLa() {
        return this.zzbrH;
    }

    public zza zzLb() {
        return this.zzbrI;
    }

    public zza zzLc() {
        return this.zzbrK;
    }

    public zza zzLd() {
        return this.zzbrM;
    }

    public zza zzLe() {
        return this.zzbrN;
    }

    public zza zzLf() {
        return this.zzbrO;
    }

    public zza zzLg() {
        return this.zzbrP;
    }

    public String zzLh() {
        Pair zzpM = zzJu().zzbsf.zzpM();
        if (zzpM == null || zzpM == zzatl.zzbse) {
            return null;
        }
        String valueOf = String.valueOf(String.valueOf(zzpM.second));
        String str = (String) zzpM.first;
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()).append(valueOf).append(":").append(str).toString();
    }

    protected void zza(int i, boolean z, boolean z2, String str, Object obj, Object obj2, Object obj3) {
        if (!z && zzai(i)) {
            zzn(i, zza(false, str, obj, obj2, obj3));
        }
        if (!z2 && i >= 5) {
            zzb(i, str, obj, obj2, obj3);
        }
    }

    protected boolean zzai(int i) {
        return Log.isLoggable(this.zzaEU, i);
    }

    public void zzb(int i, String str, Object obj, Object obj2, Object obj3) {
        zzac.zzw(str);
        zzato zzLv = this.zzbpw.zzLv();
        if (zzLv == null) {
            zzn(6, "Scheduler not set. Not logging error/warn");
        } else if (zzLv.isInitialized()) {
            if (i < 0) {
                i = 0;
            }
            if (i >= "01VDIWEA?".length()) {
                i = "01VDIWEA?".length() - 1;
            }
            String valueOf = String.valueOf("2");
            char charAt = "01VDIWEA?".charAt(i);
            char c = this.zzbrG;
            long j = this.zzbpS;
            String valueOf2 = String.valueOf(zza(true, str, obj, obj2, obj3));
            valueOf = new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append(valueOf).append(charAt).append(c).append(j).append(":").append(valueOf2).toString();
            if (valueOf.length() > 1024) {
                valueOf = str.substring(0, 1024);
            }
            zzLv.zzm(new Runnable(this) {
                final /* synthetic */ zzati zzbrR;

                public void run() {
                    zzatl zzJu = this.zzbrR.zzbpw.zzJu();
                    if (zzJu.isInitialized()) {
                        zzJu.zzbsf.zzcb(valueOf);
                    } else {
                        this.zzbrR.zzn(6, "Persisted config not initialized. Not logging error/warn");
                    }
                }
            });
        } else {
            zzn(6, "Scheduler not initialized. Not logging error/warn");
        }
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    protected void zzn(int i, String str) {
        Log.println(i, this.zzaEU, str);
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
