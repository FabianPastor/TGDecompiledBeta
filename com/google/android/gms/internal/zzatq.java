package com.google.android.gms.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.common.zze;
import com.google.android.gms.internal.zzate.zza;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.telegram.messenger.exoplayer2.C;

public class zzatq extends zza {
    private final zzatp zzbpw;
    private Boolean zzbtL;
    @Nullable
    private String zzbtM;

    public zzatq(zzatp com_google_android_gms_internal_zzatp) {
        this(com_google_android_gms_internal_zzatp, null);
    }

    public zzatq(zzatp com_google_android_gms_internal_zzatp, @Nullable String str) {
        zzac.zzw(com_google_android_gms_internal_zzatp);
        this.zzbpw = com_google_android_gms_internal_zzatp;
        this.zzbtM = str;
    }

    @BinderThread
    private void zzb(zzasq com_google_android_gms_internal_zzasq, boolean z) {
        zzac.zzw(com_google_android_gms_internal_zzasq);
        zzm(com_google_android_gms_internal_zzasq.packageName, z);
        this.zzbpw.zzJp().zzgd(com_google_android_gms_internal_zzasq.zzbqf);
    }

    @BinderThread
    private void zzm(String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            this.zzbpw.zzJt().zzLa().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        try {
            zzn(str, z);
        } catch (SecurityException e) {
            this.zzbpw.zzJt().zzLa().zzj("Measurement Service called with invalid calling package. appId", zzati.zzfI(str));
            throw e;
        }
    }

    @BinderThread
    public List<zzaub> zza(final zzasq com_google_android_gms_internal_zzasq, boolean z) {
        Object e;
        zzb(com_google_android_gms_internal_zzasq, false);
        try {
            List<zzaud> list = (List) this.zzbpw.zzJs().zzd(new Callable<List<zzaud>>(this) {
                final /* synthetic */ zzatq zzbtO;

                public /* synthetic */ Object call() throws Exception {
                    return zzLO();
                }

                public List<zzaud> zzLO() throws Exception {
                    this.zzbtO.zzbpw.zzLL();
                    return this.zzbtO.zzbpw.zzJo().zzfx(com_google_android_gms_internal_zzasq.packageName);
                }
            }).get();
            List<zzaub> arrayList = new ArrayList(list.size());
            for (zzaud com_google_android_gms_internal_zzaud : list) {
                if (z || !zzaue.zzgg(com_google_android_gms_internal_zzaud.mName)) {
                    arrayList.add(new zzaub(com_google_android_gms_internal_zzaud));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbpw.zzJt().zzLa().zze("Failed to get user attributes. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbpw.zzJt().zzLa().zze("Failed to get user attributes. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName), e);
            return null;
        }
    }

    @BinderThread
    public void zza(long j, String str, String str2, String str3) {
        final String str4 = str2;
        final String str5 = str3;
        final String str6 = str;
        final long j2 = j;
        this.zzbpw.zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzatq zzbtO;

            public void run() {
                if (str4 == null) {
                    this.zzbtO.zzbpw.zzJm().zza(str5, null);
                    return;
                }
                zzf com_google_android_gms_measurement_AppMeasurement_zzf = new zzf();
                com_google_android_gms_measurement_AppMeasurement_zzf.zzbpz = str6;
                com_google_android_gms_measurement_AppMeasurement_zzf.zzbpA = str4;
                com_google_android_gms_measurement_AppMeasurement_zzf.zzbpB = j2;
                this.zzbtO.zzbpw.zzJm().zza(str5, com_google_android_gms_measurement_AppMeasurement_zzf);
            }
        });
    }

    @BinderThread
    public void zza(final zzasq com_google_android_gms_internal_zzasq) {
        zzb(com_google_android_gms_internal_zzasq, false);
        this.zzbpw.zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzatq zzbtO;

            public void run() {
                this.zzbtO.zzbpw.zzLL();
                this.zzbtO.zzbpw.zze(com_google_android_gms_internal_zzasq);
            }
        });
    }

    @BinderThread
    public void zza(final zzatb com_google_android_gms_internal_zzatb, final zzasq com_google_android_gms_internal_zzasq) {
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzb(com_google_android_gms_internal_zzasq, false);
        this.zzbpw.zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzatq zzbtO;

            public void run() {
                this.zzbtO.zzbpw.zzLL();
                this.zzbtO.zzbpw.zzb(com_google_android_gms_internal_zzatb, com_google_android_gms_internal_zzasq);
            }
        });
    }

    @BinderThread
    public void zza(final zzatb com_google_android_gms_internal_zzatb, final String str, String str2) {
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzac.zzdv(str);
        zzm(str, true);
        this.zzbpw.zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzatq zzbtO;

            public void run() {
                this.zzbtO.zzbpw.zzLL();
                this.zzbtO.zzbpw.zzb(com_google_android_gms_internal_zzatb, str);
            }
        });
    }

    @BinderThread
    public void zza(final zzaub com_google_android_gms_internal_zzaub, final zzasq com_google_android_gms_internal_zzasq) {
        zzac.zzw(com_google_android_gms_internal_zzaub);
        zzb(com_google_android_gms_internal_zzasq, false);
        if (com_google_android_gms_internal_zzaub.getValue() == null) {
            this.zzbpw.zzJs().zzm(new Runnable(this) {
                final /* synthetic */ zzatq zzbtO;

                public void run() {
                    this.zzbtO.zzbpw.zzLL();
                    this.zzbtO.zzbpw.zzc(com_google_android_gms_internal_zzaub, com_google_android_gms_internal_zzasq);
                }
            });
        } else {
            this.zzbpw.zzJs().zzm(new Runnable(this) {
                final /* synthetic */ zzatq zzbtO;

                public void run() {
                    this.zzbtO.zzbpw.zzLL();
                    this.zzbtO.zzbpw.zzb(com_google_android_gms_internal_zzaub, com_google_android_gms_internal_zzasq);
                }
            });
        }
    }

    @BinderThread
    public byte[] zza(final zzatb com_google_android_gms_internal_zzatb, final String str) {
        Object e;
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzm(str, true);
        this.zzbpw.zzJt().zzLf().zzj("Log and bundle. event", com_google_android_gms_internal_zzatb.name);
        long nanoTime = this.zzbpw.zznq().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.zzbpw.zzJs().zze(new Callable<byte[]>(this) {
                final /* synthetic */ zzatq zzbtO;

                public /* synthetic */ Object call() throws Exception {
                    return zzLN();
                }

                public byte[] zzLN() throws Exception {
                    this.zzbtO.zzbpw.zzLL();
                    return this.zzbtO.zzbpw.zza(com_google_android_gms_internal_zzatb, str);
                }
            }).get();
            if (bArr == null) {
                this.zzbpw.zzJt().zzLa().zzj("Log and bundle returned null. appId", zzati.zzfI(str));
                bArr = new byte[0];
            }
            this.zzbpw.zzJt().zzLf().zzd("Log and bundle processed. event, size, time_ms", com_google_android_gms_internal_zzatb.name, Integer.valueOf(bArr.length), Long.valueOf((this.zzbpw.zznq().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbpw.zzJt().zzLa().zzd("Failed to log and bundle. appId, event, error", zzati.zzfI(str), com_google_android_gms_internal_zzatb.name, e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbpw.zzJt().zzLa().zzd("Failed to log and bundle. appId, event, error", zzati.zzfI(str), com_google_android_gms_internal_zzatb.name, e);
            return null;
        }
    }

    @BinderThread
    public void zzb(final zzasq com_google_android_gms_internal_zzasq) {
        zzb(com_google_android_gms_internal_zzasq, false);
        this.zzbpw.zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzatq zzbtO;

            public void run() {
                this.zzbtO.zzbpw.zzLL();
                this.zzbtO.zzbpw.zzd(com_google_android_gms_internal_zzasq);
            }
        });
    }

    @BinderThread
    public String zzc(zzasq com_google_android_gms_internal_zzasq) {
        zzb(com_google_android_gms_internal_zzasq, false);
        return this.zzbpw.zzfR(com_google_android_gms_internal_zzasq.packageName);
    }

    protected void zzn(String str, boolean z) throws SecurityException {
        if (z) {
            if (this.zzbtL == null) {
                boolean z2 = "com.google.android.gms".equals(this.zzbtM) || zzx.zzf(this.zzbpw.getContext(), Binder.getCallingUid()) || com.google.android.gms.common.zzf.zzav(this.zzbpw.getContext()).zza(this.zzbpw.getContext().getPackageManager(), Binder.getCallingUid());
                this.zzbtL = Boolean.valueOf(z2);
            }
            if (this.zzbtL.booleanValue()) {
                return;
            }
        }
        if (this.zzbtM == null && zze.zzc(this.zzbpw.getContext(), Binder.getCallingUid(), str)) {
            this.zzbtM = str;
        }
        if (!str.equals(this.zzbtM)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
        }
    }
}
