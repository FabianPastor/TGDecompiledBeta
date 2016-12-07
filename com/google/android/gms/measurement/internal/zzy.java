package com.google.android.gms.measurement.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zze;
import com.google.android.gms.measurement.internal.zzm.zza;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.telegram.messenger.exoplayer.C;

public class zzy extends zza {
    private final zzx anq;
    private Boolean art;
    @Nullable
    private String aru;

    public zzy(zzx com_google_android_gms_measurement_internal_zzx) {
        this(com_google_android_gms_measurement_internal_zzx, null);
    }

    public zzy(zzx com_google_android_gms_measurement_internal_zzx, @Nullable String str) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzx);
        this.anq = com_google_android_gms_measurement_internal_zzx;
        this.aru = str;
    }

    @BinderThread
    private void zzf(AppMetadata appMetadata) {
        zzac.zzy(appMetadata);
        zzmt(appMetadata.packageName);
        this.anq.zzbvc().zzne(appMetadata.anQ);
    }

    @BinderThread
    private void zzmt(String str) throws SecurityException {
        if (TextUtils.isEmpty(str)) {
            this.anq.zzbvg().zzbwc().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        try {
            zzmu(str);
        } catch (SecurityException e) {
            this.anq.zzbvg().zzbwc().zzj("Measurement Service called with invalid calling package", str);
            throw e;
        }
    }

    @BinderThread
    public List<UserAttributeParcel> zza(final AppMetadata appMetadata, boolean z) {
        Object e;
        zzf(appMetadata);
        try {
            List<zzak> list = (List) this.anq.zzbvf().zzd(new Callable<List<zzak>>(this) {
                final /* synthetic */ zzy arw;

                public /* synthetic */ Object call() throws Exception {
                    return zzbxs();
                }

                public List<zzak> zzbxs() throws Exception {
                    this.arw.anq.zzbxp();
                    return this.arw.anq.zzbvb().zzly(appMetadata.packageName);
                }
            }).get();
            List<UserAttributeParcel> arrayList = new ArrayList(list.size());
            for (zzak com_google_android_gms_measurement_internal_zzak : list) {
                if (z || !zzal.zznh(com_google_android_gms_measurement_internal_zzak.mName)) {
                    arrayList.add(new UserAttributeParcel(com_google_android_gms_measurement_internal_zzak));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.anq.zzbvg().zzbwc().zzj("Failed to get user attributes", e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.anq.zzbvg().zzbwc().zzj("Failed to get user attributes", e);
            return null;
        }
    }

    @BinderThread
    public void zza(final AppMetadata appMetadata) {
        zzf(appMetadata);
        this.anq.zzbvf().zzm(new Runnable(this) {
            final /* synthetic */ zzy arw;

            public void run() {
                this.arw.anq.zzbxp();
                this.arw.zzms(appMetadata.anU);
                this.arw.anq.zzd(appMetadata);
            }
        });
    }

    @BinderThread
    public void zza(final EventParcel eventParcel, final AppMetadata appMetadata) {
        zzac.zzy(eventParcel);
        zzf(appMetadata);
        this.anq.zzbvf().zzm(new Runnable(this) {
            final /* synthetic */ zzy arw;

            public void run() {
                this.arw.anq.zzbxp();
                this.arw.zzms(appMetadata.anU);
                this.arw.anq.zzb(eventParcel, appMetadata);
            }
        });
    }

    @BinderThread
    public void zza(final EventParcel eventParcel, final String str, final String str2) {
        zzac.zzy(eventParcel);
        zzac.zzhz(str);
        zzmt(str);
        this.anq.zzbvf().zzm(new Runnable(this) {
            final /* synthetic */ zzy arw;

            public void run() {
                this.arw.anq.zzbxp();
                this.arw.zzms(str2);
                this.arw.anq.zzb(eventParcel, str);
            }
        });
    }

    @BinderThread
    public void zza(final UserAttributeParcel userAttributeParcel, final AppMetadata appMetadata) {
        zzac.zzy(userAttributeParcel);
        zzf(appMetadata);
        if (userAttributeParcel.getValue() == null) {
            this.anq.zzbvf().zzm(new Runnable(this) {
                final /* synthetic */ zzy arw;

                public void run() {
                    this.arw.anq.zzbxp();
                    this.arw.zzms(appMetadata.anU);
                    this.arw.anq.zzc(userAttributeParcel, appMetadata);
                }
            });
        } else {
            this.anq.zzbvf().zzm(new Runnable(this) {
                final /* synthetic */ zzy arw;

                public void run() {
                    this.arw.anq.zzbxp();
                    this.arw.zzms(appMetadata.anU);
                    this.arw.anq.zzb(userAttributeParcel, appMetadata);
                }
            });
        }
    }

    @BinderThread
    public byte[] zza(final EventParcel eventParcel, final String str) {
        Object e;
        zzac.zzhz(str);
        zzac.zzy(eventParcel);
        zzmt(str);
        this.anq.zzbvg().zzbwi().zzj("Log and bundle. event", eventParcel.name);
        long nanoTime = this.anq.zzaan().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.anq.zzbvf().zze(new Callable<byte[]>(this) {
                final /* synthetic */ zzy arw;

                public /* synthetic */ Object call() throws Exception {
                    return zzbxr();
                }

                public byte[] zzbxr() throws Exception {
                    this.arw.anq.zzbxp();
                    return this.arw.anq.zza(eventParcel, str);
                }
            }).get();
            if (bArr == null) {
                this.anq.zzbvg().zzbwc().log("Log and bundle returned null");
                bArr = new byte[0];
            }
            this.anq.zzbvg().zzbwi().zzd("Log and bundle processed. event, size, time_ms", eventParcel.name, Integer.valueOf(bArr.length), Long.valueOf((this.anq.zzaan().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.anq.zzbvg().zzbwc().zze("Failed to log and bundle. event, error", eventParcel.name, e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.anq.zzbvg().zzbwc().zze("Failed to log and bundle. event, error", eventParcel.name, e);
            return null;
        }
    }

    @BinderThread
    public void zzb(final AppMetadata appMetadata) {
        zzf(appMetadata);
        this.anq.zzbvf().zzm(new Runnable(this) {
            final /* synthetic */ zzy arw;

            public void run() {
                this.arw.anq.zzbxp();
                this.arw.zzms(appMetadata.anU);
                this.arw.anq.zzc(appMetadata);
            }
        });
    }

    @WorkerThread
    void zzms(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split(":", 2);
            if (split.length == 2) {
                try {
                    long longValue = Long.valueOf(split[0]).longValue();
                    if (longValue > 0) {
                        this.anq.zzbvh().apP.zzi(split[1], longValue);
                    } else {
                        this.anq.zzbvg().zzbwe().zzj("Combining sample with a non-positive weight", Long.valueOf(longValue));
                    }
                } catch (NumberFormatException e) {
                    this.anq.zzbvg().zzbwe().zzj("Combining sample with a non-number weight", split[0]);
                }
            }
        }
    }

    protected void zzmu(String str) throws SecurityException {
        if (this.aru == null && zze.zzb(this.anq.getContext(), Binder.getCallingUid(), str)) {
            this.aru = str;
        }
        if (!str.equals(this.aru)) {
            if (this.art == null) {
                boolean z = ("com.google.android.gms".equals(this.aru) || com.google.android.gms.common.util.zzy.zzf(this.anq.getContext(), Binder.getCallingUid())) && !this.anq.zzbxg();
                this.art = Boolean.valueOf(z);
            }
            if (!this.art.booleanValue()) {
                throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
            }
        }
    }
}
