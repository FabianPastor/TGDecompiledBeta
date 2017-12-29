package com.google.android.gms.internal;

import android.os.Binder;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.common.zzp;
import com.google.android.gms.common.zzq;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.telegram.messenger.exoplayer2.C;

public final class zzcir extends zzchf {
    private final zzcim zziwf;
    private Boolean zzjgl;
    private String zzjgm;

    public zzcir(zzcim com_google_android_gms_internal_zzcim) {
        this(com_google_android_gms_internal_zzcim, null);
    }

    private zzcir(zzcim com_google_android_gms_internal_zzcim, String str) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcim);
        this.zziwf = com_google_android_gms_internal_zzcim;
        this.zzjgm = null;
    }

    private final void zzb(zzcgi com_google_android_gms_internal_zzcgi, boolean z) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgi);
        zzf(com_google_android_gms_internal_zzcgi.packageName, false);
        this.zziwf.zzawu().zzkg(com_google_android_gms_internal_zzcgi.zzixs);
    }

    private final void zzf(String str, boolean z) {
        boolean z2 = false;
        if (TextUtils.isEmpty(str)) {
            this.zziwf.zzawy().zzazd().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        if (z) {
            try {
                if (this.zzjgl == null) {
                    if ("com.google.android.gms".equals(this.zzjgm) || zzx.zzf(this.zziwf.getContext(), Binder.getCallingUid()) || zzq.zzci(this.zziwf.getContext()).zzbq(Binder.getCallingUid())) {
                        z2 = true;
                    }
                    this.zzjgl = Boolean.valueOf(z2);
                }
                if (this.zzjgl.booleanValue()) {
                    return;
                }
            } catch (SecurityException e) {
                this.zziwf.zzawy().zzazd().zzj("Measurement Service called with invalid calling package. appId", zzchm.zzjk(str));
                throw e;
            }
        }
        if (this.zzjgm == null && zzp.zzb(this.zziwf.getContext(), Binder.getCallingUid(), str)) {
            this.zzjgm = str;
        }
        if (!str.equals(this.zzjgm)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
        }
    }

    public final List<zzcln> zza(zzcgi com_google_android_gms_internal_zzcgi, boolean z) {
        Object e;
        zzb(com_google_android_gms_internal_zzcgi, false);
        try {
            List<zzclp> list = (List) this.zziwf.zzawx().zzc(new zzcjh(this, com_google_android_gms_internal_zzcgi)).get();
            List<zzcln> arrayList = new ArrayList(list.size());
            for (zzclp com_google_android_gms_internal_zzclp : list) {
                if (z || !zzclq.zzki(com_google_android_gms_internal_zzclp.mName)) {
                    arrayList.add(new zzcln(com_google_android_gms_internal_zzclp));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zziwf.zzawy().zzazd().zze("Failed to get user attributes. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgi.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zziwf.zzawy().zzazd().zze("Failed to get user attributes. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgi.packageName), e);
            return null;
        }
    }

    public final List<zzcgl> zza(String str, String str2, zzcgi com_google_android_gms_internal_zzcgi) {
        Object e;
        zzb(com_google_android_gms_internal_zzcgi, false);
        try {
            return (List) this.zziwf.zzawx().zzc(new zzciz(this, com_google_android_gms_internal_zzcgi, str, str2)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zziwf.zzawy().zzazd().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }

    public final List<zzcln> zza(String str, String str2, String str3, boolean z) {
        Object e;
        zzf(str, true);
        try {
            List<zzclp> list = (List) this.zziwf.zzawx().zzc(new zzciy(this, str, str2, str3)).get();
            List<zzcln> arrayList = new ArrayList(list.size());
            for (zzclp com_google_android_gms_internal_zzclp : list) {
                if (z || !zzclq.zzki(com_google_android_gms_internal_zzclp.mName)) {
                    arrayList.add(new zzcln(com_google_android_gms_internal_zzclp));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zziwf.zzawy().zzazd().zze("Failed to get user attributes. appId", zzchm.zzjk(str), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zziwf.zzawy().zzazd().zze("Failed to get user attributes. appId", zzchm.zzjk(str), e);
            return Collections.emptyList();
        }
    }

    public final List<zzcln> zza(String str, String str2, boolean z, zzcgi com_google_android_gms_internal_zzcgi) {
        Object e;
        zzb(com_google_android_gms_internal_zzcgi, false);
        try {
            List<zzclp> list = (List) this.zziwf.zzawx().zzc(new zzcix(this, com_google_android_gms_internal_zzcgi, str, str2)).get();
            List<zzcln> arrayList = new ArrayList(list.size());
            for (zzclp com_google_android_gms_internal_zzclp : list) {
                if (z || !zzclq.zzki(com_google_android_gms_internal_zzclp.mName)) {
                    arrayList.add(new zzcln(com_google_android_gms_internal_zzclp));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zziwf.zzawy().zzazd().zze("Failed to get user attributes. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgi.packageName), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zziwf.zzawy().zzazd().zze("Failed to get user attributes. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgi.packageName), e);
            return Collections.emptyList();
        }
    }

    public final void zza(long j, String str, String str2, String str3) {
        this.zziwf.zzawx().zzg(new zzcjj(this, str2, str3, str, j));
    }

    public final void zza(zzcgi com_google_android_gms_internal_zzcgi) {
        zzb(com_google_android_gms_internal_zzcgi, false);
        Runnable com_google_android_gms_internal_zzcji = new zzcji(this, com_google_android_gms_internal_zzcgi);
        if (this.zziwf.zzawx().zzazs()) {
            com_google_android_gms_internal_zzcji.run();
        } else {
            this.zziwf.zzawx().zzg(com_google_android_gms_internal_zzcji);
        }
    }

    public final void zza(zzcgl com_google_android_gms_internal_zzcgl, zzcgi com_google_android_gms_internal_zzcgi) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl);
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl.zziyg);
        zzb(com_google_android_gms_internal_zzcgi, false);
        zzcgl com_google_android_gms_internal_zzcgl2 = new zzcgl(com_google_android_gms_internal_zzcgl);
        com_google_android_gms_internal_zzcgl2.packageName = com_google_android_gms_internal_zzcgi.packageName;
        if (com_google_android_gms_internal_zzcgl.zziyg.getValue() == null) {
            this.zziwf.zzawx().zzg(new zzcit(this, com_google_android_gms_internal_zzcgl2, com_google_android_gms_internal_zzcgi));
        } else {
            this.zziwf.zzawx().zzg(new zzciu(this, com_google_android_gms_internal_zzcgl2, com_google_android_gms_internal_zzcgi));
        }
    }

    public final void zza(zzcha com_google_android_gms_internal_zzcha, zzcgi com_google_android_gms_internal_zzcgi) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcha);
        zzb(com_google_android_gms_internal_zzcgi, false);
        this.zziwf.zzawx().zzg(new zzcjc(this, com_google_android_gms_internal_zzcha, com_google_android_gms_internal_zzcgi));
    }

    public final void zza(zzcha com_google_android_gms_internal_zzcha, String str, String str2) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcha);
        zzbq.zzgm(str);
        zzf(str, true);
        this.zziwf.zzawx().zzg(new zzcjd(this, com_google_android_gms_internal_zzcha, str));
    }

    public final void zza(zzcln com_google_android_gms_internal_zzcln, zzcgi com_google_android_gms_internal_zzcgi) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcln);
        zzb(com_google_android_gms_internal_zzcgi, false);
        if (com_google_android_gms_internal_zzcln.getValue() == null) {
            this.zziwf.zzawx().zzg(new zzcjf(this, com_google_android_gms_internal_zzcln, com_google_android_gms_internal_zzcgi));
        } else {
            this.zziwf.zzawx().zzg(new zzcjg(this, com_google_android_gms_internal_zzcln, com_google_android_gms_internal_zzcgi));
        }
    }

    public final byte[] zza(zzcha com_google_android_gms_internal_zzcha, String str) {
        Object e;
        zzbq.zzgm(str);
        zzbq.checkNotNull(com_google_android_gms_internal_zzcha);
        zzf(str, true);
        this.zziwf.zzawy().zzazi().zzj("Log and bundle. event", this.zziwf.zzawt().zzjh(com_google_android_gms_internal_zzcha.name));
        long nanoTime = this.zziwf.zzws().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.zziwf.zzawx().zzd(new zzcje(this, com_google_android_gms_internal_zzcha, str)).get();
            if (bArr == null) {
                this.zziwf.zzawy().zzazd().zzj("Log and bundle returned null. appId", zzchm.zzjk(str));
                bArr = new byte[0];
            }
            this.zziwf.zzawy().zzazi().zzd("Log and bundle processed. event, size, time_ms", this.zziwf.zzawt().zzjh(com_google_android_gms_internal_zzcha.name), Integer.valueOf(bArr.length), Long.valueOf((this.zziwf.zzws().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zziwf.zzawy().zzazd().zzd("Failed to log and bundle. appId, event, error", zzchm.zzjk(str), this.zziwf.zzawt().zzjh(com_google_android_gms_internal_zzcha.name), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zziwf.zzawy().zzazd().zzd("Failed to log and bundle. appId, event, error", zzchm.zzjk(str), this.zziwf.zzawt().zzjh(com_google_android_gms_internal_zzcha.name), e);
            return null;
        }
    }

    public final void zzb(zzcgi com_google_android_gms_internal_zzcgi) {
        zzb(com_google_android_gms_internal_zzcgi, false);
        this.zziwf.zzawx().zzg(new zzcis(this, com_google_android_gms_internal_zzcgi));
    }

    public final void zzb(zzcgl com_google_android_gms_internal_zzcgl) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl);
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl.zziyg);
        zzf(com_google_android_gms_internal_zzcgl.packageName, true);
        zzcgl com_google_android_gms_internal_zzcgl2 = new zzcgl(com_google_android_gms_internal_zzcgl);
        if (com_google_android_gms_internal_zzcgl.zziyg.getValue() == null) {
            this.zziwf.zzawx().zzg(new zzciv(this, com_google_android_gms_internal_zzcgl2));
        } else {
            this.zziwf.zzawx().zzg(new zzciw(this, com_google_android_gms_internal_zzcgl2));
        }
    }

    public final String zzc(zzcgi com_google_android_gms_internal_zzcgi) {
        zzb(com_google_android_gms_internal_zzcgi, false);
        return this.zziwf.zzjx(com_google_android_gms_internal_zzcgi.packageName);
    }

    public final void zzd(zzcgi com_google_android_gms_internal_zzcgi) {
        zzf(com_google_android_gms_internal_zzcgi.packageName, false);
        this.zziwf.zzawx().zzg(new zzcjb(this, com_google_android_gms_internal_zzcgi));
    }

    public final List<zzcgl> zzj(String str, String str2, String str3) {
        Object e;
        zzf(str, true);
        try {
            return (List) this.zziwf.zzawx().zzc(new zzcja(this, str, str2, str3)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zziwf.zzawy().zzazd().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }
}
