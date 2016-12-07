package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import java.util.Collections;
import java.util.Map;

public abstract class zzk<T> implements Comparable<zzk<T>> {
    private final zza zzB;
    private final int zzC;
    private final String zzD;
    private final int zzE;
    private final com.google.android.gms.internal.zzm.zza zzF;
    private Integer zzG;
    private zzl zzH;
    private boolean zzI;
    private boolean zzJ;
    private boolean zzK;
    private long zzL;
    private zzo zzM;
    private com.google.android.gms.internal.zzb.zza zzN;

    public enum zza {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public zzk(int i, String str, com.google.android.gms.internal.zzm.zza com_google_android_gms_internal_zzm_zza) {
        this.zzB = zza.zzai ? new zza() : null;
        this.zzI = true;
        this.zzJ = false;
        this.zzK = false;
        this.zzL = 0;
        this.zzN = null;
        this.zzC = i;
        this.zzD = str;
        this.zzF = com_google_android_gms_internal_zzm_zza;
        zza(new zzd());
        this.zzE = zzb(str);
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
        return zzc((zzk) obj);
    }

    public Map<String, String> getHeaders() throws zza {
        return Collections.emptyMap();
    }

    public int getMethod() {
        return this.zzC;
    }

    public String getUrl() {
        return this.zzD;
    }

    public String toString() {
        String str = "0x";
        String valueOf = String.valueOf(Integer.toHexString(zzf()));
        valueOf = valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
        str = "[ ] ";
        String valueOf2 = String.valueOf(getUrl());
        String valueOf3 = String.valueOf(zzo());
        String valueOf4 = String.valueOf(this.zzG);
        return new StringBuilder(((((String.valueOf(str).length() + 3) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()).append(str).append(valueOf2).append(" ").append(valueOf).append(" ").append(valueOf3).append(" ").append(valueOf4).toString();
    }

    public final zzk<?> zza(int i) {
        this.zzG = Integer.valueOf(i);
        return this;
    }

    public zzk<?> zza(com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        this.zzN = com_google_android_gms_internal_zzb_zza;
        return this;
    }

    public zzk<?> zza(zzl com_google_android_gms_internal_zzl) {
        this.zzH = com_google_android_gms_internal_zzl;
        return this;
    }

    public zzk<?> zza(zzo com_google_android_gms_internal_zzo) {
        this.zzM = com_google_android_gms_internal_zzo;
        return this;
    }

    protected abstract zzm<T> zza(zzi com_google_android_gms_internal_zzi);

    protected abstract void zza(T t);

    protected zzr zzb(zzr com_google_android_gms_internal_zzr) {
        return com_google_android_gms_internal_zzr;
    }

    public int zzc(zzk<T> com_google_android_gms_internal_zzk_T) {
        zza zzo = zzo();
        zza zzo2 = com_google_android_gms_internal_zzk_T.zzo();
        return zzo == zzo2 ? this.zzG.intValue() - com_google_android_gms_internal_zzk_T.zzG.intValue() : zzo2.ordinal() - zzo.ordinal();
    }

    public void zzc(zzr com_google_android_gms_internal_zzr) {
        if (this.zzF != null) {
            this.zzF.zze(com_google_android_gms_internal_zzr);
        }
    }

    public void zzc(String str) {
        if (zza.zzai) {
            this.zzB.zza(str, Thread.currentThread().getId());
        } else if (this.zzL == 0) {
            this.zzL = SystemClock.elapsedRealtime();
        }
    }

    void zzd(final String str) {
        if (this.zzH != null) {
            this.zzH.zzf(this);
        }
        if (zza.zzai) {
            final long id = Thread.currentThread().getId();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                    final /* synthetic */ zzk zzQ;

                    public void run() {
                        this.zzQ.zzB.zza(str, id);
                        this.zzQ.zzB.zzd(toString());
                    }
                });
                return;
            }
            this.zzB.zza(str, id);
            this.zzB.zzd(toString());
            return;
        }
        if (SystemClock.elapsedRealtime() - this.zzL >= 3000) {
            zzs.zzb("%d ms: %s", Long.valueOf(SystemClock.elapsedRealtime() - this.zzL), toString());
        }
    }

    public int zzf() {
        return this.zzE;
    }

    public String zzg() {
        return getUrl();
    }

    public com.google.android.gms.internal.zzb.zza zzh() {
        return this.zzN;
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
        String str = "application/x-www-form-urlencoded; charset=";
        String valueOf = String.valueOf(zzk());
        return valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
    }

    public byte[] zzm() throws zza {
        return null;
    }

    public final boolean zzn() {
        return this.zzI;
    }

    public zza zzo() {
        return zza.NORMAL;
    }

    public final int zzp() {
        return this.zzM.zzc();
    }

    public zzo zzq() {
        return this.zzM;
    }

    public void zzr() {
        this.zzK = true;
    }

    public boolean zzs() {
        return this.zzK;
    }
}
