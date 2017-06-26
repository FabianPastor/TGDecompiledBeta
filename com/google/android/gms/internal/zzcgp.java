package com.google.android.gms.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzw;
import com.google.android.gms.common.zzo;
import com.google.android.gms.common.zzp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.telegram.messenger.exoplayer2.C;

public final class zzcgp extends zzcfd {
    private final zzcgk zzboe;
    private Boolean zzbtc;
    @Nullable
    private String zzbtd;

    public zzcgp(zzcgk com_google_android_gms_internal_zzcgk) {
        this(com_google_android_gms_internal_zzcgk, null);
    }

    private zzcgp(zzcgk com_google_android_gms_internal_zzcgk, @Nullable String str) {
        zzbo.zzu(com_google_android_gms_internal_zzcgk);
        this.zzboe = com_google_android_gms_internal_zzcgk;
        this.zzbtd = null;
    }

    @BinderThread
    private final void zzb(zzceg com_google_android_gms_internal_zzceg, boolean z) {
        zzbo.zzu(com_google_android_gms_internal_zzceg);
        zzh(com_google_android_gms_internal_zzceg.packageName, false);
        this.zzboe.zzwB().zzev(com_google_android_gms_internal_zzceg.zzboQ);
    }

    @BinderThread
    private final void zzh(String str, boolean z) {
        boolean z2 = false;
        if (TextUtils.isEmpty(str)) {
            this.zzboe.zzwF().zzyx().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        if (z) {
            try {
                if (this.zzbtc == null) {
                    if ("com.google.android.gms".equals(this.zzbtd) || zzw.zzf(this.zzboe.getContext(), Binder.getCallingUid()) || zzp.zzax(this.zzboe.getContext()).zza(this.zzboe.getContext().getPackageManager(), Binder.getCallingUid())) {
                        z2 = true;
                    }
                    this.zzbtc = Boolean.valueOf(z2);
                }
                if (this.zzbtc.booleanValue()) {
                    return;
                }
            } catch (SecurityException e) {
                this.zzboe.zzwF().zzyx().zzj("Measurement Service called with invalid calling package. appId", zzcfk.zzdZ(str));
                throw e;
            }
        }
        if (this.zzbtd == null && zzo.zzb(this.zzboe.getContext(), Binder.getCallingUid(), str)) {
            this.zzbtd = str;
        }
        if (!str.equals(this.zzbtd)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
        }
    }

    @BinderThread
    public final List<zzcjh> zza(zzceg com_google_android_gms_internal_zzceg, boolean z) {
        Object e;
        zzb(com_google_android_gms_internal_zzceg, false);
        try {
            List<zzcjj> list = (List) this.zzboe.zzwE().zze(new zzche(this, com_google_android_gms_internal_zzceg)).get();
            List<zzcjh> arrayList = new ArrayList(list.size());
            for (zzcjj com_google_android_gms_internal_zzcjj : list) {
                if (z || !zzcjk.zzex(com_google_android_gms_internal_zzcjj.mName)) {
                    arrayList.add(new zzcjh(com_google_android_gms_internal_zzcjj));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), e);
            return null;
        }
    }

    @BinderThread
    public final List<zzcej> zza(String str, String str2, zzceg com_google_android_gms_internal_zzceg) {
        Object e;
        zzb(com_google_android_gms_internal_zzceg, false);
        try {
            return (List) this.zzboe.zzwE().zze(new zzcgx(this, com_google_android_gms_internal_zzceg, str, str2)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzboe.zzwF().zzyx().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }

    @BinderThread
    public final List<zzcjh> zza(String str, String str2, String str3, boolean z) {
        Object e;
        zzh(str, true);
        try {
            List<zzcjj> list = (List) this.zzboe.zzwE().zze(new zzcgw(this, str, str2, str3)).get();
            List<zzcjh> arrayList = new ArrayList(list.size());
            for (zzcjj com_google_android_gms_internal_zzcjj : list) {
                if (z || !zzcjk.zzex(com_google_android_gms_internal_zzcjj.mName)) {
                    arrayList.add(new zzcjh(com_google_android_gms_internal_zzcjj));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfk.zzdZ(str), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfk.zzdZ(str), e);
            return Collections.emptyList();
        }
    }

    @BinderThread
    public final List<zzcjh> zza(String str, String str2, boolean z, zzceg com_google_android_gms_internal_zzceg) {
        Object e;
        zzb(com_google_android_gms_internal_zzceg, false);
        try {
            List<zzcjj> list = (List) this.zzboe.zzwE().zze(new zzcgv(this, com_google_android_gms_internal_zzceg, str, str2)).get();
            List<zzcjh> arrayList = new ArrayList(list.size());
            for (zzcjj com_google_android_gms_internal_zzcjj : list) {
                if (z || !zzcjk.zzex(com_google_android_gms_internal_zzcjj.mName)) {
                    arrayList.add(new zzcjh(com_google_android_gms_internal_zzcjj));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceg.packageName), e);
            return Collections.emptyList();
        }
    }

    @BinderThread
    public final void zza(long j, String str, String str2, String str3) {
        this.zzboe.zzwE().zzj(new zzchg(this, str2, str3, str, j));
    }

    @BinderThread
    public final void zza(zzceg com_google_android_gms_internal_zzceg) {
        zzb(com_google_android_gms_internal_zzceg, false);
        Runnable com_google_android_gms_internal_zzchf = new zzchf(this, com_google_android_gms_internal_zzceg);
        if (this.zzboe.zzwE().zzyM()) {
            com_google_android_gms_internal_zzchf.run();
        } else {
            this.zzboe.zzwE().zzj(com_google_android_gms_internal_zzchf);
        }
    }

    @BinderThread
    public final void zza(zzcej com_google_android_gms_internal_zzcej, zzceg com_google_android_gms_internal_zzceg) {
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        zzbo.zzu(com_google_android_gms_internal_zzcej.zzbpd);
        zzb(com_google_android_gms_internal_zzceg, false);
        zzcej com_google_android_gms_internal_zzcej2 = new zzcej(com_google_android_gms_internal_zzcej);
        com_google_android_gms_internal_zzcej2.packageName = com_google_android_gms_internal_zzceg.packageName;
        if (com_google_android_gms_internal_zzcej.zzbpd.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzcgr(this, com_google_android_gms_internal_zzcej2, com_google_android_gms_internal_zzceg));
        } else {
            this.zzboe.zzwE().zzj(new zzcgs(this, com_google_android_gms_internal_zzcej2, com_google_android_gms_internal_zzceg));
        }
    }

    @BinderThread
    public final void zza(zzcey com_google_android_gms_internal_zzcey, zzceg com_google_android_gms_internal_zzceg) {
        zzbo.zzu(com_google_android_gms_internal_zzcey);
        zzb(com_google_android_gms_internal_zzceg, false);
        this.zzboe.zzwE().zzj(new zzcgz(this, com_google_android_gms_internal_zzcey, com_google_android_gms_internal_zzceg));
    }

    @BinderThread
    public final void zza(zzcey com_google_android_gms_internal_zzcey, String str, String str2) {
        zzbo.zzu(com_google_android_gms_internal_zzcey);
        zzbo.zzcF(str);
        zzh(str, true);
        this.zzboe.zzwE().zzj(new zzcha(this, com_google_android_gms_internal_zzcey, str));
    }

    @BinderThread
    public final void zza(zzcjh com_google_android_gms_internal_zzcjh, zzceg com_google_android_gms_internal_zzceg) {
        zzbo.zzu(com_google_android_gms_internal_zzcjh);
        zzb(com_google_android_gms_internal_zzceg, false);
        if (com_google_android_gms_internal_zzcjh.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzchc(this, com_google_android_gms_internal_zzcjh, com_google_android_gms_internal_zzceg));
        } else {
            this.zzboe.zzwE().zzj(new zzchd(this, com_google_android_gms_internal_zzcjh, com_google_android_gms_internal_zzceg));
        }
    }

    @BinderThread
    public final byte[] zza(zzcey com_google_android_gms_internal_zzcey, String str) {
        Object e;
        zzbo.zzcF(str);
        zzbo.zzu(com_google_android_gms_internal_zzcey);
        zzh(str, true);
        this.zzboe.zzwF().zzyC().zzj("Log and bundle. event", this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcey.name));
        long nanoTime = this.zzboe.zzkq().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.zzboe.zzwE().zzf(new zzchb(this, com_google_android_gms_internal_zzcey, str)).get();
            if (bArr == null) {
                this.zzboe.zzwF().zzyx().zzj("Log and bundle returned null. appId", zzcfk.zzdZ(str));
                bArr = new byte[0];
            }
            this.zzboe.zzwF().zzyC().zzd("Log and bundle processed. event, size, time_ms", this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcey.name), Integer.valueOf(bArr.length), Long.valueOf((this.zzboe.zzkq().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zzd("Failed to log and bundle. appId, event, error", zzcfk.zzdZ(str), this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcey.name), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zzd("Failed to log and bundle. appId, event, error", zzcfk.zzdZ(str), this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcey.name), e);
            return null;
        }
    }

    @BinderThread
    public final void zzb(zzceg com_google_android_gms_internal_zzceg) {
        zzb(com_google_android_gms_internal_zzceg, false);
        this.zzboe.zzwE().zzj(new zzcgq(this, com_google_android_gms_internal_zzceg));
    }

    @BinderThread
    public final void zzb(zzcej com_google_android_gms_internal_zzcej) {
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        zzbo.zzu(com_google_android_gms_internal_zzcej.zzbpd);
        zzh(com_google_android_gms_internal_zzcej.packageName, true);
        zzcej com_google_android_gms_internal_zzcej2 = new zzcej(com_google_android_gms_internal_zzcej);
        if (com_google_android_gms_internal_zzcej.zzbpd.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzcgt(this, com_google_android_gms_internal_zzcej2));
        } else {
            this.zzboe.zzwE().zzj(new zzcgu(this, com_google_android_gms_internal_zzcej2));
        }
    }

    @BinderThread
    public final String zzc(zzceg com_google_android_gms_internal_zzceg) {
        zzb(com_google_android_gms_internal_zzceg, false);
        return this.zzboe.zzem(com_google_android_gms_internal_zzceg.packageName);
    }

    @BinderThread
    public final List<zzcej> zzk(String str, String str2, String str3) {
        Object e;
        zzh(str, true);
        try {
            return (List) this.zzboe.zzwE().zze(new zzcgy(this, str, str2, str3)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzboe.zzwF().zzyx().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }
}
