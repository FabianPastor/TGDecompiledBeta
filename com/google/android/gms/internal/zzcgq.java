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

public final class zzcgq extends zzcfe {
    private final zzcgl zzboe;
    private Boolean zzbtc;
    @Nullable
    private String zzbtd;

    public zzcgq(zzcgl com_google_android_gms_internal_zzcgl) {
        this(com_google_android_gms_internal_zzcgl, null);
    }

    private zzcgq(zzcgl com_google_android_gms_internal_zzcgl, @Nullable String str) {
        zzbo.zzu(com_google_android_gms_internal_zzcgl);
        this.zzboe = com_google_android_gms_internal_zzcgl;
        this.zzbtd = null;
    }

    @BinderThread
    private final void zzb(zzceh com_google_android_gms_internal_zzceh, boolean z) {
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        zzh(com_google_android_gms_internal_zzceh.packageName, false);
        this.zzboe.zzwB().zzev(com_google_android_gms_internal_zzceh.zzboQ);
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
                this.zzboe.zzwF().zzyx().zzj("Measurement Service called with invalid calling package. appId", zzcfl.zzdZ(str));
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
    public final List<zzcji> zza(zzceh com_google_android_gms_internal_zzceh, boolean z) {
        Object e;
        zzb(com_google_android_gms_internal_zzceh, false);
        try {
            List<zzcjk> list = (List) this.zzboe.zzwE().zze(new zzchf(this, com_google_android_gms_internal_zzceh)).get();
            List<zzcji> arrayList = new ArrayList(list.size());
            for (zzcjk com_google_android_gms_internal_zzcjk : list) {
                if (z || !zzcjl.zzex(com_google_android_gms_internal_zzcjk.mName)) {
                    arrayList.add(new zzcji(com_google_android_gms_internal_zzcjk));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), e);
            return null;
        }
    }

    @BinderThread
    public final List<zzcek> zza(String str, String str2, zzceh com_google_android_gms_internal_zzceh) {
        Object e;
        zzb(com_google_android_gms_internal_zzceh, false);
        try {
            return (List) this.zzboe.zzwE().zze(new zzcgy(this, com_google_android_gms_internal_zzceh, str, str2)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzboe.zzwF().zzyx().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }

    @BinderThread
    public final List<zzcji> zza(String str, String str2, String str3, boolean z) {
        Object e;
        zzh(str, true);
        try {
            List<zzcjk> list = (List) this.zzboe.zzwE().zze(new zzcgx(this, str, str2, str3)).get();
            List<zzcji> arrayList = new ArrayList(list.size());
            for (zzcjk com_google_android_gms_internal_zzcjk : list) {
                if (z || !zzcjl.zzex(com_google_android_gms_internal_zzcjk.mName)) {
                    arrayList.add(new zzcji(com_google_android_gms_internal_zzcjk));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(str), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(str), e);
            return Collections.emptyList();
        }
    }

    @BinderThread
    public final List<zzcji> zza(String str, String str2, boolean z, zzceh com_google_android_gms_internal_zzceh) {
        Object e;
        zzb(com_google_android_gms_internal_zzceh, false);
        try {
            List<zzcjk> list = (List) this.zzboe.zzwE().zze(new zzcgw(this, com_google_android_gms_internal_zzceh, str, str2)).get();
            List<zzcji> arrayList = new ArrayList(list.size());
            for (zzcjk com_google_android_gms_internal_zzcjk : list) {
                if (z || !zzcjl.zzex(com_google_android_gms_internal_zzcjk.mName)) {
                    arrayList.add(new zzcji(com_google_android_gms_internal_zzcjk));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zze("Failed to get user attributes. appId", zzcfl.zzdZ(com_google_android_gms_internal_zzceh.packageName), e);
            return Collections.emptyList();
        }
    }

    @BinderThread
    public final void zza(long j, String str, String str2, String str3) {
        this.zzboe.zzwE().zzj(new zzchh(this, str2, str3, str, j));
    }

    @BinderThread
    public final void zza(zzceh com_google_android_gms_internal_zzceh) {
        zzb(com_google_android_gms_internal_zzceh, false);
        Runnable com_google_android_gms_internal_zzchg = new zzchg(this, com_google_android_gms_internal_zzceh);
        if (this.zzboe.zzwE().zzyM()) {
            com_google_android_gms_internal_zzchg.run();
        } else {
            this.zzboe.zzwE().zzj(com_google_android_gms_internal_zzchg);
        }
    }

    @BinderThread
    public final void zza(zzcek com_google_android_gms_internal_zzcek, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzcek);
        zzbo.zzu(com_google_android_gms_internal_zzcek.zzbpd);
        zzb(com_google_android_gms_internal_zzceh, false);
        zzcek com_google_android_gms_internal_zzcek2 = new zzcek(com_google_android_gms_internal_zzcek);
        com_google_android_gms_internal_zzcek2.packageName = com_google_android_gms_internal_zzceh.packageName;
        if (com_google_android_gms_internal_zzcek.zzbpd.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzcgs(this, com_google_android_gms_internal_zzcek2, com_google_android_gms_internal_zzceh));
        } else {
            this.zzboe.zzwE().zzj(new zzcgt(this, com_google_android_gms_internal_zzcek2, com_google_android_gms_internal_zzceh));
        }
    }

    @BinderThread
    public final void zza(zzcez com_google_android_gms_internal_zzcez, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzcez);
        zzb(com_google_android_gms_internal_zzceh, false);
        this.zzboe.zzwE().zzj(new zzcha(this, com_google_android_gms_internal_zzcez, com_google_android_gms_internal_zzceh));
    }

    @BinderThread
    public final void zza(zzcez com_google_android_gms_internal_zzcez, String str, String str2) {
        zzbo.zzu(com_google_android_gms_internal_zzcez);
        zzbo.zzcF(str);
        zzh(str, true);
        this.zzboe.zzwE().zzj(new zzchb(this, com_google_android_gms_internal_zzcez, str));
    }

    @BinderThread
    public final void zza(zzcji com_google_android_gms_internal_zzcji, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzcji);
        zzb(com_google_android_gms_internal_zzceh, false);
        if (com_google_android_gms_internal_zzcji.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzchd(this, com_google_android_gms_internal_zzcji, com_google_android_gms_internal_zzceh));
        } else {
            this.zzboe.zzwE().zzj(new zzche(this, com_google_android_gms_internal_zzcji, com_google_android_gms_internal_zzceh));
        }
    }

    @BinderThread
    public final byte[] zza(zzcez com_google_android_gms_internal_zzcez, String str) {
        Object e;
        zzbo.zzcF(str);
        zzbo.zzu(com_google_android_gms_internal_zzcez);
        zzh(str, true);
        this.zzboe.zzwF().zzyC().zzj("Log and bundle. event", this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcez.name));
        long nanoTime = this.zzboe.zzkq().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.zzboe.zzwE().zzf(new zzchc(this, com_google_android_gms_internal_zzcez, str)).get();
            if (bArr == null) {
                this.zzboe.zzwF().zzyx().zzj("Log and bundle returned null. appId", zzcfl.zzdZ(str));
                bArr = new byte[0];
            }
            this.zzboe.zzwF().zzyC().zzd("Log and bundle processed. event, size, time_ms", this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcez.name), Integer.valueOf(bArr.length), Long.valueOf((this.zzboe.zzkq().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzboe.zzwF().zzyx().zzd("Failed to log and bundle. appId, event, error", zzcfl.zzdZ(str), this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcez.name), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzboe.zzwF().zzyx().zzd("Failed to log and bundle. appId, event, error", zzcfl.zzdZ(str), this.zzboe.zzwA().zzdW(com_google_android_gms_internal_zzcez.name), e);
            return null;
        }
    }

    @BinderThread
    public final void zzb(zzceh com_google_android_gms_internal_zzceh) {
        zzb(com_google_android_gms_internal_zzceh, false);
        this.zzboe.zzwE().zzj(new zzcgr(this, com_google_android_gms_internal_zzceh));
    }

    @BinderThread
    public final void zzb(zzcek com_google_android_gms_internal_zzcek) {
        zzbo.zzu(com_google_android_gms_internal_zzcek);
        zzbo.zzu(com_google_android_gms_internal_zzcek.zzbpd);
        zzh(com_google_android_gms_internal_zzcek.packageName, true);
        zzcek com_google_android_gms_internal_zzcek2 = new zzcek(com_google_android_gms_internal_zzcek);
        if (com_google_android_gms_internal_zzcek.zzbpd.getValue() == null) {
            this.zzboe.zzwE().zzj(new zzcgu(this, com_google_android_gms_internal_zzcek2));
        } else {
            this.zzboe.zzwE().zzj(new zzcgv(this, com_google_android_gms_internal_zzcek2));
        }
    }

    @BinderThread
    public final String zzc(zzceh com_google_android_gms_internal_zzceh) {
        zzb(com_google_android_gms_internal_zzceh, false);
        return this.zzboe.zzem(com_google_android_gms_internal_zzceh.packageName);
    }

    @BinderThread
    public final List<zzcek> zzk(String str, String str2, String str3) {
        Object e;
        zzh(str, true);
        try {
            return (List) this.zzboe.zzwE().zze(new zzcgz(this, str, str2, str3)).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzboe.zzwF().zzyx().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }
}
