package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.util.zzd;

public final class zzclf extends zzcjl {
    private Handler mHandler;
    private long zzjjc = zzws().elapsedRealtime();
    private final zzcgs zzjjd = new zzclg(this, this.zziwf);
    private final zzcgs zzjje = new zzclh(this, this.zziwf);

    zzclf(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final void zzbav() {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    private final void zzbaw() {
        zzve();
        zzbs(false);
        zzawk().zzaj(zzws().elapsedRealtime());
    }

    private final void zzbe(long j) {
        zzve();
        zzbav();
        this.zzjjd.cancel();
        this.zzjje.cancel();
        zzawy().zzazj().zzj("Activity resumed, time", Long.valueOf(j));
        this.zzjjc = j;
        if (zzws().currentTimeMillis() - zzawz().zzjdf.get() > zzawz().zzjdh.get()) {
            zzawz().zzjdg.set(true);
            zzawz().zzjdi.set(0);
        }
        if (zzawz().zzjdg.get()) {
            this.zzjjd.zzs(Math.max(0, zzawz().zzjde.get() - zzawz().zzjdi.get()));
        } else {
            this.zzjje.zzs(Math.max(0, 3600000 - zzawz().zzjdi.get()));
        }
    }

    private final void zzbf(long j) {
        zzve();
        zzbav();
        this.zzjjd.cancel();
        this.zzjje.cancel();
        zzawy().zzazj().zzj("Activity paused, time", Long.valueOf(j));
        if (this.zzjjc != 0) {
            zzawz().zzjdi.set(zzawz().zzjdi.get() + (j - this.zzjjc));
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
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

    public final boolean zzbs(boolean z) {
        zzve();
        zzxf();
        long elapsedRealtime = zzws().elapsedRealtime();
        zzawz().zzjdh.set(zzws().currentTimeMillis());
        long j = elapsedRealtime - this.zzjjc;
        if (z || j >= 1000) {
            zzawz().zzjdi.set(j);
            zzawy().zzazj().zzj("Recording user engagement, ms", Long.valueOf(j));
            Bundle bundle = new Bundle();
            bundle.putLong("_et", j);
            zzckc.zza(zzawq().zzbao(), bundle);
            zzawm().zzc("auto", "_e", bundle);
            this.zzjjc = elapsedRealtime;
            this.zzjje.cancel();
            this.zzjje.zzs(Math.max(0, 3600000 - zzawz().zzjdi.get()));
            return true;
        }
        zzawy().zzazj().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(j));
        return false;
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
