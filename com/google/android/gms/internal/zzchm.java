package com.google.android.gms.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzchm extends zzcjl {
    private final String zzgay = ((String) zzchc.zzjad.get());
    private final long zzixd = 11910;
    private final char zzjbt;
    private final zzcho zzjbu;
    private final zzcho zzjbv;
    private final zzcho zzjbw;
    private final zzcho zzjbx;
    private final zzcho zzjby;
    private final zzcho zzjbz;
    private final zzcho zzjca;
    private final zzcho zzjcb;
    private final zzcho zzjcc;

    zzchm(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
        if (zzaxa().zzyp()) {
            this.zzjbt = 'C';
        } else {
            this.zzjbt = 'c';
        }
        this.zzjbu = new zzcho(this, 6, false, false);
        this.zzjbv = new zzcho(this, 6, true, false);
        this.zzjbw = new zzcho(this, 6, false, true);
        this.zzjbx = new zzcho(this, 5, false, false);
        this.zzjby = new zzcho(this, 5, true, false);
        this.zzjbz = new zzcho(this, 5, false, true);
        this.zzjca = new zzcho(this, 4, false, false);
        this.zzjcb = new zzcho(this, 3, false, false);
        this.zzjcc = new zzcho(this, 2, false, false);
    }

    private static String zza(boolean z, String str, Object obj, Object obj2, Object obj3) {
        if (str == null) {
            Object obj4 = TtmlNode.ANONYMOUS_REGION_ID;
        }
        Object zzb = zzb(z, obj);
        Object zzb2 = zzb(z, obj2);
        Object zzb3 = zzb(z, obj3);
        StringBuilder stringBuilder = new StringBuilder();
        String str2 = TtmlNode.ANONYMOUS_REGION_ID;
        if (!TextUtils.isEmpty(obj4)) {
            stringBuilder.append(obj4);
            str2 = ": ";
        }
        if (!TextUtils.isEmpty(zzb)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzb);
            str2 = ", ";
        }
        if (!TextUtils.isEmpty(zzb2)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzb2);
            str2 = ", ";
        }
        if (!TextUtils.isEmpty(zzb3)) {
            stringBuilder.append(str2);
            stringBuilder.append(zzb3);
        }
        return stringBuilder.toString();
    }

    private static String zzb(boolean z, Object obj) {
        if (obj == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        Object valueOf = obj instanceof Integer ? Long.valueOf((long) ((Integer) obj).intValue()) : obj;
        if (valueOf instanceof Long) {
            if (!z) {
                return String.valueOf(valueOf);
            }
            if (Math.abs(((Long) valueOf).longValue()) < 100) {
                return String.valueOf(valueOf);
            }
            String str = String.valueOf(valueOf).charAt(0) == '-' ? "-" : TtmlNode.ANONYMOUS_REGION_ID;
            String valueOf2 = String.valueOf(Math.abs(((Long) valueOf).longValue()));
            return new StringBuilder((String.valueOf(str).length() + 43) + String.valueOf(str).length()).append(str).append(Math.round(Math.pow(10.0d, (double) (valueOf2.length() - 1)))).append("...").append(str).append(Math.round(Math.pow(10.0d, (double) valueOf2.length()) - 1.0d)).toString();
        } else if (valueOf instanceof Boolean) {
            return String.valueOf(valueOf);
        } else {
            if (!(valueOf instanceof Throwable)) {
                return valueOf instanceof zzchp ? ((zzchp) valueOf).zzgxt : z ? "-" : String.valueOf(valueOf);
            } else {
                Throwable th = (Throwable) valueOf;
                StringBuilder stringBuilder = new StringBuilder(z ? th.getClass().getName() : th.toString());
                String zzjl = zzjl(AppMeasurement.class.getCanonicalName());
                String zzjl2 = zzjl(zzcim.class.getCanonicalName());
                for (StackTraceElement stackTraceElement : th.getStackTrace()) {
                    if (!stackTraceElement.isNativeMethod()) {
                        String className = stackTraceElement.getClassName();
                        if (className != null) {
                            className = zzjl(className);
                            if (className.equals(zzjl) || className.equals(zzjl2)) {
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

    protected static Object zzjk(String str) {
        return str == null ? null : new zzchp(str);
    }

    private static String zzjl(String str) {
        if (TextUtils.isEmpty(str)) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        int lastIndexOf = str.lastIndexOf(46);
        return lastIndexOf != -1 ? str.substring(0, lastIndexOf) : str;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    protected final void zza(int i, boolean z, boolean z2, String str, Object obj, Object obj2, Object obj3) {
        if (!z && zzae(i)) {
            zzk(i, zza(false, str, obj, obj2, obj3));
        }
        if (!z2 && i >= 5) {
            zzbq.checkNotNull(str);
            zzcjl zzazy = this.zziwf.zzazy();
            if (zzazy == null) {
                zzk(6, "Scheduler not set. Not logging error/warn");
            } else if (zzazy.isInitialized()) {
                int i2 = i < 0 ? 0 : i;
                if (i2 >= 9) {
                    i2 = 8;
                }
                String str2 = "2";
                char charAt = "01VDIWEA?".charAt(i2);
                char c = this.zzjbt;
                long j = this.zzixd;
                String zza = zza(true, str, obj, obj2, obj3);
                String stringBuilder = new StringBuilder((String.valueOf(str2).length() + 23) + String.valueOf(zza).length()).append(str2).append(charAt).append(c).append(j).append(":").append(zza).toString();
                if (stringBuilder.length() > 1024) {
                    stringBuilder = str.substring(0, 1024);
                }
                zzazy.zzg(new zzchn(this, stringBuilder));
            } else {
                zzk(6, "Scheduler not initialized. Not logging error/warn");
            }
        }
    }

    protected final boolean zzae(int i) {
        return Log.isLoggable(this.zzgay, i);
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

    public final zzcho zzazd() {
        return this.zzjbu;
    }

    public final zzcho zzaze() {
        return this.zzjbv;
    }

    public final zzcho zzazf() {
        return this.zzjbx;
    }

    public final zzcho zzazg() {
        return this.zzjbz;
    }

    public final zzcho zzazh() {
        return this.zzjca;
    }

    public final zzcho zzazi() {
        return this.zzjcb;
    }

    public final zzcho zzazj() {
        return this.zzjcc;
    }

    public final String zzazk() {
        Pair zzaad = zzawz().zzjcq.zzaad();
        if (zzaad == null || zzaad == zzchx.zzjcp) {
            return null;
        }
        String valueOf = String.valueOf(zzaad.second);
        String str = (String) zzaad.first;
        return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()).append(valueOf).append(":").append(str).toString();
    }

    protected final void zzk(int i, String str) {
        Log.println(i, this.zzgay, str);
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
