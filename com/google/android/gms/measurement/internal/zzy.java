package com.google.android.gms.measurement.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.common.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import com.google.android.gms.measurement.internal.zzm.zza;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.telegram.messenger.exoplayer2.C;

public class zzy extends zza {
    private final zzx aqw;
    private Boolean auE;
    @Nullable
    private String auF;

    public zzy(zzx com_google_android_gms_measurement_internal_zzx) {
        this(com_google_android_gms_measurement_internal_zzx, null);
    }

    public zzy(zzx com_google_android_gms_measurement_internal_zzx, @Nullable String str) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzx);
        this.aqw = com_google_android_gms_measurement_internal_zzx;
        this.auF = str;
    }

    @BinderThread
    private void zzb(AppMetadata appMetadata, boolean z) {
        zzaa.zzy(appMetadata);
        zzn(appMetadata.packageName, z);
        this.aqw.zzbvx().zznb(appMetadata.aqZ);
    }

    @BinderThread
    private void zzn(String str, boolean z) throws SecurityException {
        if (TextUtils.isEmpty(str)) {
            this.aqw.zzbwb().zzbwy().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        try {
            zzo(str, z);
        } catch (SecurityException e) {
            this.aqw.zzbwb().zzbwy().zzj("Measurement Service called with invalid calling package", str);
            throw e;
        }
    }

    @BinderThread
    public List<UserAttributeParcel> zza(final AppMetadata appMetadata, boolean z) {
        Object e;
        zzb(appMetadata, false);
        try {
            List<zzak> list = (List) this.aqw.zzbwa().zzd(new Callable<List<zzak>>(this) {
                final /* synthetic */ zzy auH;

                public /* synthetic */ Object call() throws Exception {
                    return zzbym();
                }

                public List<zzak> zzbym() throws Exception {
                    this.auH.aqw.zzbyj();
                    return this.auH.aqw.zzbvw().zzly(appMetadata.packageName);
                }
            }).get();
            List<UserAttributeParcel> arrayList = new ArrayList(list.size());
            for (zzak com_google_android_gms_measurement_internal_zzak : list) {
                if (z || !zzal.zzne(com_google_android_gms_measurement_internal_zzak.mName)) {
                    arrayList.add(new UserAttributeParcel(com_google_android_gms_measurement_internal_zzak));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.aqw.zzbwb().zzbwy().zzj("Failed to get user attributes", e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.aqw.zzbwb().zzbwy().zzj("Failed to get user attributes", e);
            return null;
        }
    }

    @BinderThread
    public void zza(long j, String str, String str2, String str3) {
        final String str4 = str2;
        final String str5 = str3;
        final String str6 = str;
        final long j2 = j;
        this.aqw.zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzy auH;

            public void run() {
                if (str4 == null) {
                    this.auH.aqw.zzbvu().zza(str5, null);
                    return;
                }
                zzf com_google_android_gms_measurement_AppMeasurement_zzf = new zzf();
                com_google_android_gms_measurement_AppMeasurement_zzf.aqz = str6;
                com_google_android_gms_measurement_AppMeasurement_zzf.aqA = str4;
                com_google_android_gms_measurement_AppMeasurement_zzf.aqB = j2;
                this.auH.aqw.zzbvu().zza(str5, com_google_android_gms_measurement_AppMeasurement_zzf);
            }
        });
    }

    @BinderThread
    public void zza(final AppMetadata appMetadata) {
        zzb(appMetadata, false);
        this.aqw.zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzy auH;

            public void run() {
                this.auH.aqw.zzbyj();
                this.auH.zzmr(appMetadata.ard);
                this.auH.aqw.zzd(appMetadata);
            }
        });
    }

    @BinderThread
    public void zza(final EventParcel eventParcel, final AppMetadata appMetadata) {
        zzaa.zzy(eventParcel);
        zzb(appMetadata, false);
        this.aqw.zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzy auH;

            public void run() {
                this.auH.aqw.zzbyj();
                this.auH.zzmr(appMetadata.ard);
                this.auH.aqw.zzb(eventParcel, appMetadata);
            }
        });
    }

    @BinderThread
    public void zza(final EventParcel eventParcel, final String str, final String str2) {
        zzaa.zzy(eventParcel);
        zzaa.zzib(str);
        zzn(str, true);
        this.aqw.zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzy auH;

            public void run() {
                this.auH.aqw.zzbyj();
                this.auH.zzmr(str2);
                this.auH.aqw.zzb(eventParcel, str);
            }
        });
    }

    @BinderThread
    public void zza(final UserAttributeParcel userAttributeParcel, final AppMetadata appMetadata) {
        zzaa.zzy(userAttributeParcel);
        zzb(appMetadata, false);
        if (userAttributeParcel.getValue() == null) {
            this.aqw.zzbwa().zzm(new Runnable(this) {
                final /* synthetic */ zzy auH;

                public void run() {
                    this.auH.aqw.zzbyj();
                    this.auH.zzmr(appMetadata.ard);
                    this.auH.aqw.zzc(userAttributeParcel, appMetadata);
                }
            });
        } else {
            this.aqw.zzbwa().zzm(new Runnable(this) {
                final /* synthetic */ zzy auH;

                public void run() {
                    this.auH.aqw.zzbyj();
                    this.auH.zzmr(appMetadata.ard);
                    this.auH.aqw.zzb(userAttributeParcel, appMetadata);
                }
            });
        }
    }

    @BinderThread
    public byte[] zza(final EventParcel eventParcel, final String str) {
        Object e;
        zzaa.zzib(str);
        zzaa.zzy(eventParcel);
        zzn(str, true);
        this.aqw.zzbwb().zzbxd().zzj("Log and bundle. event", eventParcel.name);
        long nanoTime = this.aqw.zzabz().nanoTime() / C.MICROS_PER_SECOND;
        try {
            byte[] bArr = (byte[]) this.aqw.zzbwa().zze(new Callable<byte[]>(this) {
                final /* synthetic */ zzy auH;

                public /* synthetic */ Object call() throws Exception {
                    return zzbyl();
                }

                public byte[] zzbyl() throws Exception {
                    this.auH.aqw.zzbyj();
                    return this.auH.aqw.zza(eventParcel, str);
                }
            }).get();
            if (bArr == null) {
                this.aqw.zzbwb().zzbwy().log("Log and bundle returned null");
                bArr = new byte[0];
            }
            this.aqw.zzbwb().zzbxd().zzd("Log and bundle processed. event, size, time_ms", eventParcel.name, Integer.valueOf(bArr.length), Long.valueOf((this.aqw.zzabz().nanoTime() / C.MICROS_PER_SECOND) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.aqw.zzbwb().zzbwy().zze("Failed to log and bundle. event, error", eventParcel.name, e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.aqw.zzbwb().zzbwy().zze("Failed to log and bundle. event, error", eventParcel.name, e);
            return null;
        }
    }

    @BinderThread
    public void zzb(final AppMetadata appMetadata) {
        zzb(appMetadata, false);
        this.aqw.zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzy auH;

            public void run() {
                this.auH.aqw.zzbyj();
                this.auH.zzmr(appMetadata.ard);
                this.auH.aqw.zzc(appMetadata);
            }
        });
    }

    @WorkerThread
    void zzmr(String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split(":", 2);
            if (split.length == 2) {
                try {
                    long longValue = Long.valueOf(split[0]).longValue();
                    if (longValue > 0) {
                        this.aqw.zzbwc().asY.zzg(split[1], longValue);
                    } else {
                        this.aqw.zzbwb().zzbxa().zzj("Combining sample with a non-positive weight", Long.valueOf(longValue));
                    }
                } catch (NumberFormatException e) {
                    this.aqw.zzbwb().zzbxa().zzj("Combining sample with a non-number weight", split[0]);
                }
            }
        }
    }

    protected void zzo(String str, boolean z) throws SecurityException {
        if (z) {
            if (this.auE == null) {
                boolean z2;
                if ("com.google.android.gms".equals(this.auF) || zzx.zzf(this.aqw.getContext(), Binder.getCallingUid()) || com.google.android.gms.common.zzf.zzbv(this.aqw.getContext()).zza(this.aqw.getContext().getPackageManager(), Binder.getCallingUid())) {
                    zzx com_google_android_gms_measurement_internal_zzx = this.aqw;
                    z2 = true;
                } else {
                    z2 = false;
                }
                this.auE = Boolean.valueOf(z2);
            }
            if (this.auE.booleanValue()) {
                return;
            }
        }
        if (this.auF == null && zze.zzc(this.aqw.getContext(), Binder.getCallingUid(), str)) {
            this.auF = str;
        }
        if (!str.equals(this.auF)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
        }
    }
}
