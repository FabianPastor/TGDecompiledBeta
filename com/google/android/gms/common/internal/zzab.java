package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zze;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.TimeUnit;

public class zzab {
    private static final zzb zzaGw = new zzb() {
        public com.google.android.gms.common.api.zza zzH(Status status) {
            return zzb.zzG(status);
        }
    };

    public interface zza<R extends Result, T> {
        T zzf(R r);
    }

    public interface zzb {
        com.google.android.gms.common.api.zza zzH(Status status);
    }

    class AnonymousClass2 implements com.google.android.gms.common.api.PendingResult.zza {
        final /* synthetic */ zzb zzaGA;
        final /* synthetic */ PendingResult zzaGx;
        final /* synthetic */ TaskCompletionSource zzaGy;
        final /* synthetic */ zza zzaGz;

        AnonymousClass2(PendingResult pendingResult, TaskCompletionSource taskCompletionSource, zza com_google_android_gms_common_internal_zzab_zza, zzb com_google_android_gms_common_internal_zzab_zzb) {
            this.zzaGx = pendingResult;
            this.zzaGy = taskCompletionSource;
            this.zzaGz = com_google_android_gms_common_internal_zzab_zza;
            this.zzaGA = com_google_android_gms_common_internal_zzab_zzb;
        }

        public void zzy(Status status) {
            if (status.isSuccess()) {
                this.zzaGy.setResult(this.zzaGz.zzf(this.zzaGx.await(0, TimeUnit.MILLISECONDS)));
                return;
            }
            this.zzaGy.setException(this.zzaGA.zzH(status));
        }
    }

    class AnonymousClass3 implements zza<R, T> {
        final /* synthetic */ zze zzaGB;

        AnonymousClass3(zze com_google_android_gms_common_api_zze) {
            this.zzaGB = com_google_android_gms_common_api_zze;
        }

        public T zze(R r) {
            this.zzaGB.zzb(r);
            return this.zzaGB;
        }

        public /* synthetic */ Object zzf(Result result) {
            return zze(result);
        }
    }

    public static <R extends Result, T extends zze<R>> Task<T> zza(PendingResult<R> pendingResult, T t) {
        return zza((PendingResult) pendingResult, new AnonymousClass3(t));
    }

    public static <R extends Result, T> Task<T> zza(PendingResult<R> pendingResult, zza<R, T> com_google_android_gms_common_internal_zzab_zza_R__T) {
        return zza(pendingResult, com_google_android_gms_common_internal_zzab_zza_R__T, zzaGw);
    }

    public static <R extends Result, T> Task<T> zza(PendingResult<R> pendingResult, zza<R, T> com_google_android_gms_common_internal_zzab_zza_R__T, zzb com_google_android_gms_common_internal_zzab_zzb) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        pendingResult.zza(new AnonymousClass2(pendingResult, taskCompletionSource, com_google_android_gms_common_internal_zzab_zza_R__T, com_google_android_gms_common_internal_zzab_zzb));
        return taskCompletionSource.getTask();
    }
}
