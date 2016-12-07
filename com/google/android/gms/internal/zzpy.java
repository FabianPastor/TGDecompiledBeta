package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseException;
import java.util.Map;

public abstract class zzpy {
    public final int lN;
    public final int wf;

    private static abstract class zza extends zzpy {
        protected final SparseArray<Map<com.google.android.gms.internal.zzrd.zzb<?>, zzri>> wg;
        protected final TaskCompletionSource<Void> wh;

        public zza(int i, int i2, TaskCompletionSource<Void> taskCompletionSource, SparseArray<Map<com.google.android.gms.internal.zzrd.zzb<?>, zzri>> sparseArray) {
            super(i, i2);
            this.wg = sparseArray;
            this.wh = taskCompletionSource;
        }

        private void zza(RemoteException remoteException) {
            zzx(new Status(8, remoteException.getLocalizedMessage(), null));
        }

        public boolean cancel() {
            this.wh.setException(new com.google.android.gms.common.api.zza(Status.wc));
            return true;
        }

        public void zza(SparseArray<zzrq> sparseArray) {
        }

        protected abstract void zza(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws RemoteException;

        public final void zzb(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws DeadObjectException {
            try {
                zza(com_google_android_gms_common_api_Api_zzb);
            } catch (RemoteException e) {
                zza(e);
                throw e;
            } catch (RemoteException e2) {
                zza(e2);
            }
        }

        public void zzx(@NonNull Status status) {
            this.wh.setException(new com.google.android.gms.common.api.zza(status));
        }
    }

    public static class zzb<A extends com.google.android.gms.internal.zzqc.zza<? extends Result, com.google.android.gms.common.api.Api.zzb>> extends zzpy {
        protected final A wi;

        public zzb(int i, int i2, A a) {
            super(i, i2);
            this.wi = a;
        }

        public boolean cancel() {
            return this.wi.zzaqq();
        }

        public void zza(SparseArray<zzrq> sparseArray) {
            zzrq com_google_android_gms_internal_zzrq = (zzrq) sparseArray.get(this.wf);
            if (com_google_android_gms_internal_zzrq != null) {
                com_google_android_gms_internal_zzrq.zzb(this.wi);
            }
        }

        public void zzb(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws DeadObjectException {
            this.wi.zzb(com_google_android_gms_common_api_Api_zzb);
        }

        public void zzx(@NonNull Status status) {
            this.wi.zzz(status);
        }
    }

    public static final class zzd<TResult> extends zzpy {
        private static final Status wm = new Status(8, "Connection to Google Play services was lost while executing the API call.");
        private final TaskCompletionSource<TResult> wh;
        private final zzro<com.google.android.gms.common.api.Api.zzb, TResult> wl;

        public zzd(int i, int i2, zzro<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzro_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource) {
            super(i, i2);
            this.wh = taskCompletionSource;
            this.wl = com_google_android_gms_internal_zzro_com_google_android_gms_common_api_Api_zzb__TResult;
        }

        public void zzb(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws DeadObjectException {
            try {
                this.wl.zzb(com_google_android_gms_common_api_Api_zzb, this.wh);
            } catch (DeadObjectException e) {
                zzx(wm);
                throw e;
            } catch (RemoteException e2) {
                zzx(wm);
            }
        }

        public void zzx(@NonNull Status status) {
            if (status.getStatusCode() == 8) {
                this.wh.setException(new FirebaseException(status.getStatusMessage()));
            } else {
                this.wh.setException(new FirebaseApiNotAvailableException(status.getStatusMessage()));
            }
        }
    }

    public static final class zzc extends zza {
        public final zzrh<com.google.android.gms.common.api.Api.zzb> wj;
        public final zzrr<com.google.android.gms.common.api.Api.zzb> wk;

        public zzc(int i, zzri com_google_android_gms_internal_zzri, TaskCompletionSource<Void> taskCompletionSource, SparseArray<Map<com.google.android.gms.internal.zzrd.zzb<?>, zzri>> sparseArray) {
            super(i, 3, taskCompletionSource, sparseArray);
            this.wj = com_google_android_gms_internal_zzri.wj;
            this.wk = com_google_android_gms_internal_zzri.wk;
        }

        public /* bridge */ /* synthetic */ boolean cancel() {
            return super.cancel();
        }

        public void zza(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws DeadObjectException {
            this.wj.zza(com_google_android_gms_common_api_Api_zzb, this.wh);
            Map map = (Map) this.wg.get(this.wf);
            if (map == null) {
                map = new ArrayMap(1);
                this.wg.put(this.wf, map);
            }
            String valueOf = String.valueOf(this.wj.zzasr());
            Log.d("reg", new StringBuilder(String.valueOf(valueOf).length() + 12).append("registered: ").append(valueOf).toString());
            if (this.wj.zzasr() != null) {
                map.put(this.wj.zzasr(), new zzri(this.wj, this.wk));
            }
        }

        public /* bridge */ /* synthetic */ void zzx(@NonNull Status status) {
            super.zzx(status);
        }
    }

    public static final class zze extends zza {
        public final zzrr<com.google.android.gms.common.api.Api.zzb> wn;

        public zze(int i, zzrr<com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzrr_com_google_android_gms_common_api_Api_zzb, TaskCompletionSource<Void> taskCompletionSource, SparseArray<Map<com.google.android.gms.internal.zzrd.zzb<?>, zzri>> sparseArray) {
            super(i, 4, taskCompletionSource, sparseArray);
            this.wn = com_google_android_gms_internal_zzrr_com_google_android_gms_common_api_Api_zzb;
        }

        public /* bridge */ /* synthetic */ boolean cancel() {
            return super.cancel();
        }

        public void zza(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws DeadObjectException {
            Map map = (Map) this.wg.get(this.wf);
            if (map == null || this.wn.zzasr() == null) {
                Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
                this.wh.setException(new com.google.android.gms.common.api.zza(Status.wa));
                return;
            }
            map.remove(this.wn.zzasr());
            this.wn.zzc(com_google_android_gms_common_api_Api_zzb, this.wh);
        }

        public /* bridge */ /* synthetic */ void zzx(@NonNull Status status) {
            super.zzx(status);
        }
    }

    public zzpy(int i, int i2) {
        this.wf = i;
        this.lN = i2;
    }

    public boolean cancel() {
        return true;
    }

    public void zza(SparseArray<zzrq> sparseArray) {
    }

    public abstract void zzb(com.google.android.gms.common.api.Api.zzb com_google_android_gms_common_api_Api_zzb) throws DeadObjectException;

    public abstract void zzx(@NonNull Status status);
}
