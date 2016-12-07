package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzqj {
    public final int nV;

    private static abstract class zza extends zzqj {
        protected final TaskCompletionSource<Void> yg;

        public zza(int i, TaskCompletionSource<Void> taskCompletionSource) {
            super(i);
            this.yg = taskCompletionSource;
        }

        private void zza(RemoteException remoteException) {
            zzy(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        public void zza(@NonNull zzqv com_google_android_gms_internal_zzqv, boolean z) {
        }

        public final void zza(zza<?> com_google_android_gms_internal_zzrh_zza_) throws DeadObjectException {
            try {
                zzb(com_google_android_gms_internal_zzrh_zza_);
            } catch (RemoteException e) {
                zza(e);
                throw e;
            } catch (RemoteException e2) {
                zza(e2);
            }
        }

        protected abstract void zzb(zza<?> com_google_android_gms_internal_zzrh_zza_) throws RemoteException;

        public void zzy(@NonNull Status status) {
            this.yg.trySetException(new com.google.android.gms.common.api.zza(status));
        }
    }

    public static class zzb<A extends com.google.android.gms.internal.zzqo.zza<? extends Result, com.google.android.gms.common.api.Api.zzb>> extends zzqj {
        protected final A yh;

        public zzb(int i, A a) {
            super(i);
            this.yh = a;
        }

        public void zza(@NonNull zzqv com_google_android_gms_internal_zzqv, boolean z) {
            com_google_android_gms_internal_zzqv.zza(this.yh, z);
        }

        public void zza(zza<?> com_google_android_gms_internal_zzrh_zza_) throws DeadObjectException {
            this.yh.zzb(com_google_android_gms_internal_zzrh_zza_.getClient());
        }

        public void zzy(@NonNull Status status) {
            this.yh.zzaa(status);
        }
    }

    public static final class zzd<TResult> extends zzqj {
        private static final Status ym = new Status(8, "Connection to Google Play services was lost while executing the API call.");
        private final TaskCompletionSource<TResult> yg;
        private final zzse<com.google.android.gms.common.api.Api.zzb, TResult> yk;
        private final zzsb yl;

        public zzd(int i, zzse<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzse_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzsb com_google_android_gms_internal_zzsb) {
            super(i);
            this.yg = taskCompletionSource;
            this.yk = com_google_android_gms_internal_zzse_com_google_android_gms_common_api_Api_zzb__TResult;
            this.yl = com_google_android_gms_internal_zzsb;
        }

        public void zza(@NonNull zzqv com_google_android_gms_internal_zzqv, boolean z) {
            com_google_android_gms_internal_zzqv.zza(this.yg, z);
        }

        public void zza(zza<?> com_google_android_gms_internal_zzrh_zza_) throws DeadObjectException {
            try {
                this.yk.zzb(com_google_android_gms_internal_zzrh_zza_.getClient(), this.yg);
            } catch (DeadObjectException e) {
                throw e;
            } catch (RemoteException e2) {
                zzy(ym);
            }
        }

        public void zzy(@NonNull Status status) {
            this.yg.trySetException(this.yl.zzz(status));
        }
    }

    public static final class zzc extends zza {
        public final zzrw<com.google.android.gms.common.api.Api.zzb> yi;
        public final zzsh<com.google.android.gms.common.api.Api.zzb> yj;

        public zzc(zzrx com_google_android_gms_internal_zzrx, TaskCompletionSource<Void> taskCompletionSource) {
            super(3, taskCompletionSource);
            this.yi = com_google_android_gms_internal_zzrx.yi;
            this.yj = com_google_android_gms_internal_zzrx.yj;
        }

        public /* bridge */ /* synthetic */ void zza(@NonNull zzqv com_google_android_gms_internal_zzqv, boolean z) {
            super.zza(com_google_android_gms_internal_zzqv, z);
        }

        public void zzb(zza<?> com_google_android_gms_internal_zzrh_zza_) throws DeadObjectException {
            this.yi.zza(com_google_android_gms_internal_zzrh_zza_.getClient(), this.yg);
            if (this.yi.zzatz() != null) {
                com_google_android_gms_internal_zzrh_zza_.zzatn().put(this.yi.zzatz(), new zzrx(this.yi, this.yj));
            }
        }

        public /* bridge */ /* synthetic */ void zzy(@NonNull Status status) {
            super.zzy(status);
        }
    }

    public static final class zze extends zza {
        public final com.google.android.gms.internal.zzrr.zzb<?> yn;

        public zze(com.google.android.gms.internal.zzrr.zzb<?> com_google_android_gms_internal_zzrr_zzb_, TaskCompletionSource<Void> taskCompletionSource) {
            super(4, taskCompletionSource);
            this.yn = com_google_android_gms_internal_zzrr_zzb_;
        }

        public /* bridge */ /* synthetic */ void zza(@NonNull zzqv com_google_android_gms_internal_zzqv, boolean z) {
            super.zza(com_google_android_gms_internal_zzqv, z);
        }

        public void zzb(zza<?> com_google_android_gms_internal_zzrh_zza_) throws DeadObjectException {
            zzrx com_google_android_gms_internal_zzrx = (zzrx) com_google_android_gms_internal_zzrh_zza_.zzatn().remove(this.yn);
            if (com_google_android_gms_internal_zzrx != null) {
                com_google_android_gms_internal_zzrx.yj.zzc(com_google_android_gms_internal_zzrh_zza_.getClient(), this.yg);
                com_google_android_gms_internal_zzrx.yi.zzaua();
                return;
            }
            Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
            this.yg.trySetException(new com.google.android.gms.common.api.zza(Status.yb));
        }

        public /* bridge */ /* synthetic */ void zzy(@NonNull Status status) {
            super.zzy(status);
        }
    }

    public zzqj(int i) {
        this.nV = i;
    }

    public abstract void zza(@NonNull zzqv com_google_android_gms_internal_zzqv, boolean z);

    public abstract void zza(zza<?> com_google_android_gms_internal_zzrh_zza_) throws DeadObjectException;

    public abstract void zzy(@NonNull Status status);
}
