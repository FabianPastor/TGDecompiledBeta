package com.google.android.gms.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzy;
import com.google.android.gms.common.zzg;
import com.google.android.gms.common.zzh;
import com.google.android.gms.internal.zzatt.zza;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.telegram.messenger.exoplayer2.C;

public class zzauf extends zza {
    private final zzaue zzbqg;
    private Boolean zzbuN;
    @Nullable
    private String zzbuO;

    public zzauf(zzaue com_google_android_gms_internal_zzaue) {
        this(com_google_android_gms_internal_zzaue, null);
    }

    public zzauf(zzaue com_google_android_gms_internal_zzaue, @Nullable String str) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        this.zzbqg = com_google_android_gms_internal_zzaue;
        this.zzbuO = str;
    }

    @BinderThread
    private void zzb(zzatd com_google_android_gms_internal_zzatd, boolean z) {
        zzac.zzw(com_google_android_gms_internal_zzatd);
        zzm(com_google_android_gms_internal_zzatd.packageName, z);
        this.zzbqg.zzKg().zzga(com_google_android_gms_internal_zzatd.zzbqP);
    }

    @BinderThread
    private void zzm(String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            this.zzbqg.zzKk().zzLX().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        try {
            zzn(str, z);
        } catch (SecurityException e) {
            this.zzbqg.zzKk().zzLX().zzj("Measurement Service called with invalid calling package. appId", zzatx.zzfE(str));
            throw e;
        }
    }

    @BinderThread
    public List<zzauq> zza(final zzatd com_google_android_gms_internal_zzatd, boolean z) {
        Object e;
        zzb(com_google_android_gms_internal_zzatd, false);
        try {
            List<zzaus> list = (List) this.zzbqg.zzKj().zzd(new Callable<List<zzaus>>(this) {
                final /* synthetic */ zzauf zzbuQ;

                public /* synthetic */ Object call() throws Exception {
                    return zzMM();
                }

                public List<zzaus> zzMM() throws Exception {
                    this.zzbuQ.zzbqg.zzMK();
                    return this.zzbuQ.zzbqg.zzKf().zzft(com_google_android_gms_internal_zzatd.packageName);
                }
            }).get();
            List<zzauq> arrayList = new ArrayList(list.size());
            for (zzaus com_google_android_gms_internal_zzaus : list) {
                if (z || !zzaut.zzgd(com_google_android_gms_internal_zzaus.mName)) {
                    arrayList.add(new zzauq(com_google_android_gms_internal_zzaus));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbqg.zzKk().zzLX().zze("Failed to get user attributes. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbqg.zzKk().zzLX().zze("Failed to get user attributes. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
            return null;
        }
    }

    @BinderThread
    public List<zzatg> zza(final String str, final String str2, final zzatd com_google_android_gms_internal_zzatd) {
        Object e;
        zzb(com_google_android_gms_internal_zzatd, false);
        try {
            return (List) this.zzbqg.zzKj().zzd(new Callable<List<zzatg>>(this) {
                final /* synthetic */ zzauf zzbuQ;

                public /* synthetic */ Object call() throws Exception {
                    return zzMM();
                }

                public List<zzatg> zzMM() throws Exception {
                    this.zzbuQ.zzbqg.zzMK();
                    return this.zzbuQ.zzbqg.zzKf().zzl(com_google_android_gms_internal_zzatd.packageName, str, str2);
                }
            }).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzbqg.zzKk().zzLX().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }

    @BinderThread
    public List<zzauq> zza(final String str, final String str2, final String str3, boolean z) {
        Object e;
        zzm(str, true);
        try {
            List<zzaus> list = (List) this.zzbqg.zzKj().zzd(new Callable<List<zzaus>>(this) {
                final /* synthetic */ zzauf zzbuQ;

                public /* synthetic */ Object call() throws Exception {
                    return zzMM();
                }

                public List<zzaus> zzMM() throws Exception {
                    this.zzbuQ.zzbqg.zzMK();
                    return this.zzbuQ.zzbqg.zzKf().zzk(str, str2, str3);
                }
            }).get();
            List<zzauq> arrayList = new ArrayList(list.size());
            for (zzaus com_google_android_gms_internal_zzaus : list) {
                if (z || !zzaut.zzgd(com_google_android_gms_internal_zzaus.mName)) {
                    arrayList.add(new zzauq(com_google_android_gms_internal_zzaus));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbqg.zzKk().zzLX().zze("Failed to get user attributes. appId", zzatx.zzfE(str), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbqg.zzKk().zzLX().zze("Failed to get user attributes. appId", zzatx.zzfE(str), e);
            return Collections.emptyList();
        }
    }

    @BinderThread
    public List<zzauq> zza(final String str, final String str2, boolean z, final zzatd com_google_android_gms_internal_zzatd) {
        Object e;
        zzb(com_google_android_gms_internal_zzatd, false);
        try {
            List<zzaus> list = (List) this.zzbqg.zzKj().zzd(new Callable<List<zzaus>>(this) {
                final /* synthetic */ zzauf zzbuQ;

                public /* synthetic */ Object call() throws Exception {
                    return zzMM();
                }

                public List<zzaus> zzMM() throws Exception {
                    this.zzbuQ.zzbqg.zzMK();
                    return this.zzbuQ.zzbqg.zzKf().zzk(com_google_android_gms_internal_zzatd.packageName, str, str2);
                }
            }).get();
            List<zzauq> arrayList = new ArrayList(list.size());
            for (zzaus com_google_android_gms_internal_zzaus : list) {
                if (z || !zzaut.zzgd(com_google_android_gms_internal_zzaus.mName)) {
                    arrayList.add(new zzauq(com_google_android_gms_internal_zzaus));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbqg.zzKk().zzLX().zze("Failed to get user attributes. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
            return Collections.emptyList();
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbqg.zzKk().zzLX().zze("Failed to get user attributes. appId", zzatx.zzfE(com_google_android_gms_internal_zzatd.packageName), e);
            return Collections.emptyList();
        }
    }

    @BinderThread
    public void zza(long j, String str, String str2, String str3) {
        final String str4 = str2;
        final String str5 = str3;
        final String str6 = str;
        final long j2 = j;
        this.zzbqg.zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzauf zzbuQ;

            public void run() {
                if (str4 == null) {
                    this.zzbuQ.zzbqg.zzKd().zza(str5, null);
                    return;
                }
                zzf com_google_android_gms_measurement_AppMeasurement_zzf = new zzf();
                com_google_android_gms_measurement_AppMeasurement_zzf.zzbqj = str6;
                com_google_android_gms_measurement_AppMeasurement_zzf.zzbqk = str4;
                com_google_android_gms_measurement_AppMeasurement_zzf.zzbql = j2;
                this.zzbuQ.zzbqg.zzKd().zza(str5, com_google_android_gms_measurement_AppMeasurement_zzf);
            }
        });
    }

    @BinderThread
    public void zza(final zzatd com_google_android_gms_internal_zzatd) {
        zzb(com_google_android_gms_internal_zzatd, false);
        this.zzbqg.zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzauf zzbuQ;

            public void run() {
                this.zzbuQ.zzbqg.zzMK();
                this.zzbuQ.zzbqg.zze(com_google_android_gms_internal_zzatd);
            }
        });
    }

    @BinderThread
    public void zza(zzatg com_google_android_gms_internal_zzatg, final zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbra);
        zzb(com_google_android_gms_internal_zzatd, false);
        final zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
        com_google_android_gms_internal_zzatg2.packageName = com_google_android_gms_internal_zzatd.packageName;
        if (com_google_android_gms_internal_zzatg.zzbra.getValue() == null) {
            this.zzbqg.zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauf zzbuQ;

                public void run() {
                    this.zzbuQ.zzbqg.zzMK();
                    this.zzbuQ.zzbqg.zzc(com_google_android_gms_internal_zzatg2, com_google_android_gms_internal_zzatd);
                }
            });
        } else {
            this.zzbqg.zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauf zzbuQ;

                public void run() {
                    this.zzbuQ.zzbqg.zzMK();
                    this.zzbuQ.zzbqg.zzb(com_google_android_gms_internal_zzatg2, com_google_android_gms_internal_zzatd);
                }
            });
        }
    }

    @BinderThread
    public void zza(final zzatq com_google_android_gms_internal_zzatq, final zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzb(com_google_android_gms_internal_zzatd, false);
        this.zzbqg.zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzauf zzbuQ;

            public void run() {
                this.zzbuQ.zzbqg.zzMK();
                this.zzbuQ.zzbqg.zzb(com_google_android_gms_internal_zzatq, com_google_android_gms_internal_zzatd);
            }
        });
    }

    @BinderThread
    public void zza(final zzatq com_google_android_gms_internal_zzatq, final String str, String str2) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzac.zzdr(str);
        zzm(str, true);
        this.zzbqg.zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzauf zzbuQ;

            public void run() {
                this.zzbuQ.zzbqg.zzMK();
                this.zzbuQ.zzbqg.zzb(com_google_android_gms_internal_zzatq, str);
            }
        });
    }

    @BinderThread
    public void zza(final zzauq com_google_android_gms_internal_zzauq, final zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzauq);
        zzb(com_google_android_gms_internal_zzatd, false);
        if (com_google_android_gms_internal_zzauq.getValue() == null) {
            this.zzbqg.zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauf zzbuQ;

                public void run() {
                    this.zzbuQ.zzbqg.zzMK();
                    this.zzbuQ.zzbqg.zzc(com_google_android_gms_internal_zzauq, com_google_android_gms_internal_zzatd);
                }
            });
        } else {
            this.zzbqg.zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauf zzbuQ;

                public void run() {
                    this.zzbuQ.zzbqg.zzMK();
                    this.zzbuQ.zzbqg.zzb(com_google_android_gms_internal_zzauq, com_google_android_gms_internal_zzatd);
                }
            });
        }
    }

    @BinderThread
    public byte[] zza(final zzatq com_google_android_gms_internal_zzatq, final String str) {
        Object e;
        zzac.zzdr(str);
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzm(str, true);
        this.zzbqg.zzKk().zzMc().zzj("Log and bundle. event", com_google_android_gms_internal_zzatq.name);
        long nanoTime = this.zzbqg.zznR().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.zzbqg.zzKj().zze(new Callable<byte[]>(this) {
                final /* synthetic */ zzauf zzbuQ;

                public /* synthetic */ Object call() throws Exception {
                    return zzMN();
                }

                public byte[] zzMN() throws Exception {
                    this.zzbuQ.zzbqg.zzMK();
                    return this.zzbuQ.zzbqg.zza(com_google_android_gms_internal_zzatq, str);
                }
            }).get();
            if (bArr == null) {
                this.zzbqg.zzKk().zzLX().zzj("Log and bundle returned null. appId", zzatx.zzfE(str));
                bArr = new byte[0];
            }
            this.zzbqg.zzKk().zzMc().zzd("Log and bundle processed. event, size, time_ms", com_google_android_gms_internal_zzatq.name, Integer.valueOf(bArr.length), Long.valueOf((this.zzbqg.zznR().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbqg.zzKk().zzLX().zzd("Failed to log and bundle. appId, event, error", zzatx.zzfE(str), com_google_android_gms_internal_zzatq.name, e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbqg.zzKk().zzLX().zzd("Failed to log and bundle. appId, event, error", zzatx.zzfE(str), com_google_android_gms_internal_zzatq.name, e);
            return null;
        }
    }

    @BinderThread
    public void zzb(final zzatd com_google_android_gms_internal_zzatd) {
        zzb(com_google_android_gms_internal_zzatd, false);
        this.zzbqg.zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzauf zzbuQ;

            public void run() {
                this.zzbuQ.zzbqg.zzMK();
                this.zzbuQ.zzbqg.zzd(com_google_android_gms_internal_zzatd);
            }
        });
    }

    @BinderThread
    public void zzb(zzatg com_google_android_gms_internal_zzatg) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzac.zzw(com_google_android_gms_internal_zzatg.zzbra);
        zzm(com_google_android_gms_internal_zzatg.packageName, true);
        final zzatg com_google_android_gms_internal_zzatg2 = new zzatg(com_google_android_gms_internal_zzatg);
        if (com_google_android_gms_internal_zzatg.zzbra.getValue() == null) {
            this.zzbqg.zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauf zzbuQ;

                public void run() {
                    this.zzbuQ.zzbqg.zzMK();
                    this.zzbuQ.zzbqg.zze(com_google_android_gms_internal_zzatg2);
                }
            });
        } else {
            this.zzbqg.zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauf zzbuQ;

                public void run() {
                    this.zzbuQ.zzbqg.zzMK();
                    this.zzbuQ.zzbqg.zzd(com_google_android_gms_internal_zzatg2);
                }
            });
        }
    }

    @BinderThread
    public String zzc(zzatd com_google_android_gms_internal_zzatd) {
        zzb(com_google_android_gms_internal_zzatd, false);
        return this.zzbqg.zzfP(com_google_android_gms_internal_zzatd.packageName);
    }

    @BinderThread
    public List<zzatg> zzn(final String str, final String str2, final String str3) {
        Object e;
        zzm(str, true);
        try {
            return (List) this.zzbqg.zzKj().zzd(new Callable<List<zzatg>>(this) {
                final /* synthetic */ zzauf zzbuQ;

                public /* synthetic */ Object call() throws Exception {
                    return zzMM();
                }

                public List<zzatg> zzMM() throws Exception {
                    this.zzbuQ.zzbqg.zzMK();
                    return this.zzbuQ.zzbqg.zzKf().zzl(str, str2, str3);
                }
            }).get();
        } catch (InterruptedException e2) {
            e = e2;
        } catch (ExecutionException e3) {
            e = e3;
        }
        this.zzbqg.zzKk().zzLX().zzj("Failed to get conditional user properties", e);
        return Collections.emptyList();
    }

    protected void zzn(String str, boolean z) throws SecurityException {
        if (z) {
            if (this.zzbuN == null) {
                boolean z2 = "com.google.android.gms".equals(this.zzbuO) || zzy.zzf(this.zzbqg.getContext(), Binder.getCallingUid()) || zzh.zzaN(this.zzbqg.getContext()).zza(this.zzbqg.getContext().getPackageManager(), Binder.getCallingUid());
                this.zzbuN = Boolean.valueOf(z2);
            }
            if (this.zzbuN.booleanValue()) {
                return;
            }
        }
        if (this.zzbuO == null && zzg.zzc(this.zzbqg.getContext(), Binder.getCallingUid(), str)) {
            this.zzbuO = str;
        }
        if (!str.equals(this.zzbuO)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
        }
    }
}
