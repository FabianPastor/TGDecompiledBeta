package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import java.util.Collections;
import java.util.Map;

public abstract class zzl<T> implements Comparable<zzl<T>> {
    private final zza zzC;
    private final int zzD;
    private final String zzE;
    private final int zzF;
    private final com.google.android.gms.internal.zzn.zza zzG;
    private Integer zzH;
    private zzm zzI;
    private boolean zzJ;
    private boolean zzK;
    private boolean zzL;
    private boolean zzM;
    private zzp zzN;
    private com.google.android.gms.internal.zzb.zza zzO;

    public enum zza {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public zzl(int i, String str, com.google.android.gms.internal.zzn.zza com_google_android_gms_internal_zzn_zza) {
        this.zzC = zza.zzaj ? new zza() : null;
        this.zzJ = true;
        this.zzK = false;
        this.zzL = false;
        this.zzM = false;
        this.zzO = null;
        this.zzD = i;
        this.zzE = str;
        this.zzG = com_google_android_gms_internal_zzn_zza;
        zza(new zze());
        this.zzF = zzb(str);
    }

    private static int zzb(String str) {
        if (!TextUtils.isEmpty(str)) {
            Uri parse = Uri.parse(str);
            if (parse != null) {
                String host = parse.getHost();
                if (host != null) {
                    return host.hashCode();
                }
            }
        }
        return 0;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return zzc((zzl) obj);
    }

    public Map<String, String> getHeaders() throws zza {
        return Collections.emptyMap();
    }

    public int getMethod() {
        return this.zzD;
    }

    public String getUrl() {
        return this.zzE;
    }

    public String toString() {
        return "[ ] " + getUrl() + " " + ("0x" + Integer.toHexString(zzf())) + " " + zzo() + " " + this.zzH;
    }

    public final zzl<?> zza(int i) {
        this.zzH = Integer.valueOf(i);
        return this;
    }

    public zzl<?> zza(com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        this.zzO = com_google_android_gms_internal_zzb_zza;
        return this;
    }

    public zzl<?> zza(zzm com_google_android_gms_internal_zzm) {
        this.zzI = com_google_android_gms_internal_zzm;
        return this;
    }

    public zzl<?> zza(zzp com_google_android_gms_internal_zzp) {
        this.zzN = com_google_android_gms_internal_zzp;
        return this;
    }

    protected abstract zzn<T> zza(zzj com_google_android_gms_internal_zzj);

    protected abstract void zza(T t);

    protected zzs zzb(zzs com_google_android_gms_internal_zzs) {
        return com_google_android_gms_internal_zzs;
    }

    public int zzc(zzl<T> com_google_android_gms_internal_zzl_T) {
        zza zzo = zzo();
        zza zzo2 = com_google_android_gms_internal_zzl_T.zzo();
        return zzo == zzo2 ? this.zzH.intValue() - com_google_android_gms_internal_zzl_T.zzH.intValue() : zzo2.ordinal() - zzo.ordinal();
    }

    public void zzc(zzs com_google_android_gms_internal_zzs) {
        if (this.zzG != null) {
            this.zzG.zze(com_google_android_gms_internal_zzs);
        }
    }

    public void zzc(String str) {
        if (zza.zzaj) {
            this.zzC.zza(str, Thread.currentThread().getId());
        }
    }

    void zzd(final String str) {
        if (this.zzI != null) {
            this.zzI.zzf(this);
        }
        if (zza.zzaj) {
            final long id = Thread.currentThread().getId();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                    final /* synthetic */ zzl zzR;

                    public void run() {
                        this.zzR.zzC.zza(str, id);
                        this.zzR.zzC.zzd(toString());
                    }
                });
                return;
            }
            this.zzC.zza(str, id);
            this.zzC.zzd(toString());
        }
    }

    public int zzf() {
        return this.zzF;
    }

    public String zzg() {
        return getUrl();
    }

    public com.google.android.gms.internal.zzb.zza zzh() {
        return this.zzO;
    }

    @Deprecated
    public String zzi() {
        return zzl();
    }

    @Deprecated
    public byte[] zzj() throws zza {
        return null;
    }

    protected String zzk() {
        return "UTF-8";
    }

    public String zzl() {
        return "application/x-www-form-urlencoded; charset=" + zzk();
    }

    public byte[] zzm() throws zza {
        return null;
    }

    public final boolean zzn() {
        return this.zzJ;
    }

    public zza zzo() {
        return zza.NORMAL;
    }

    public final int zzp() {
        return this.zzN.zzc();
    }

    public zzp zzq() {
        return this.zzN;
    }

    public void zzr() {
        this.zzL = true;
    }

    public boolean zzs() {
        return this.zzL;
    }
}
