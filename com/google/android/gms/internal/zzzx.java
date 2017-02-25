package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzzx {
    public final int zzakD;

    private static abstract class zza extends zzzx {
        protected final TaskCompletionSource<Void> zzazE;

        public zza(int i, TaskCompletionSource<Void> taskCompletionSource) {
            super(i);
            this.zzazE = taskCompletionSource;
        }

        public void zza(@NonNull zzaal com_google_android_gms_internal_zzaal, boolean z) {
        }

        public final void zza(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws DeadObjectException {
            try {
                zzb(com_google_android_gms_internal_zzaax_zza_);
            } catch (RemoteException e) {
                zzz(zzzx.zza(e));
                throw e;
            } catch (RemoteException e2) {
                zzz(zzzx.zza(e2));
            }
        }

        protected abstract void zzb(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws RemoteException;

        public void zzz(@NonNull Status status) {
            this.zzazE.trySetException(new com.google.android.gms.common.api.zza(status));
        }
    }

    public static class zzb<A extends com.google.android.gms.internal.zzaad.zza<? extends Result, com.google.android.gms.common.api.Api.zzb>> extends zzzx {
        protected final A zzazF;

        public zzb(int i, A a) {
            super(i);
            this.zzazF = a;
        }

        public void zza(@NonNull zzaal com_google_android_gms_internal_zzaal, boolean z) {
            com_google_android_gms_internal_zzaal.zza(this.zzazF, z);
        }

        public void zza(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws DeadObjectException {
            this.zzazF.zzb(com_google_android_gms_internal_zzaax_zza_.zzvU());
        }

        public void zzz(@NonNull Status status) {
            this.zzazF.zzB(status);
        }
    }

    public static final class zzd<TResult> extends zzzx {
        private final TaskCompletionSource<TResult> zzazE;
        private final zzabv<com.google.android.gms.common.api.Api.zzb, TResult> zzazI;
        private final zzabs zzazJ;

        public zzd(int i, zzabv<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzabv_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzabs com_google_android_gms_internal_zzabs) {
            super(i);
            this.zzazE = taskCompletionSource;
            this.zzazI = com_google_android_gms_internal_zzabv_com_google_android_gms_common_api_Api_zzb__TResult;
            this.zzazJ = com_google_android_gms_internal_zzabs;
        }

        public void zza(@NonNull zzaal com_google_android_gms_internal_zzaal, boolean z) {
            com_google_android_gms_internal_zzaal.zza(this.zzazE, z);
        }

        public void zza(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws DeadObjectException {
            try {
                this.zzazI.zza(com_google_android_gms_internal_zzaax_zza_.zzvU(), this.zzazE);
            } catch (DeadObjectException e) {
                throw e;
            } catch (RemoteException e2) {
                zzz(zzzx.zza(e2));
            }
        }

        public void zzz(@NonNull Status status) {
            this.zzazE.trySetException(this.zzazJ.zzA(status));
        }
    }

    public static final class zzc extends zza {
        public final zzabm<com.google.android.gms.common.api.Api.zzb, ?> zzazG;
        public final zzabz<com.google.android.gms.common.api.Api.zzb, ?> zzazH;

        public zzc(zzabn com_google_android_gms_internal_zzabn, TaskCompletionSource<Void> taskCompletionSource) {
            super(3, taskCompletionSource);
            this.zzazG = com_google_android_gms_internal_zzabn.zzazG;
            this.zzazH = com_google_android_gms_internal_zzabn.zzazH;
        }

        public /* bridge */ /* synthetic */ void zza(@NonNull zzaal com_google_android_gms_internal_zzaal, boolean z) {
            super.zza(com_google_android_gms_internal_zzaal, z);
        }

        public void zzb(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws RemoteException {
            if (this.zzazG.zzwW() != null) {
                com_google_android_gms_internal_zzaax_zza_.zzwI().put(this.zzazG.zzwW(), new zzabn(this.zzazG, this.zzazH));
            }
        }

        public /* bridge */ /* synthetic */ void zzz(@NonNull Status status) {
            super.zzz(status);
        }
    }

    public static final class zze extends zza {
        public final com.google.android.gms.internal.zzabh.zzb<?> zzazK;

        public zze(com.google.android.gms.internal.zzabh.zzb<?> com_google_android_gms_internal_zzabh_zzb_, TaskCompletionSource<Void> taskCompletionSource) {
            super(4, taskCompletionSource);
            this.zzazK = com_google_android_gms_internal_zzabh_zzb_;
        }

        public /* bridge */ /* synthetic */ void zza(@NonNull zzaal com_google_android_gms_internal_zzaal, boolean z) {
            super.zza(com_google_android_gms_internal_zzaal, z);
        }

        public void zzb(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws RemoteException {
            zzabn com_google_android_gms_internal_zzabn = (zzabn) com_google_android_gms_internal_zzaax_zza_.zzwI().remove(this.zzazK);
            if (com_google_android_gms_internal_zzabn != null) {
                com_google_android_gms_internal_zzabn.zzazG.zzwX();
                return;
            }
            Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
            this.zzazE.trySetException(new com.google.android.gms.common.api.zza(Status.zzazz));
        }

        public /* bridge */ /* synthetic */ void zzz(@NonNull Status status) {
            super.zzz(status);
        }
    }

    public zzzx(int i) {
        this.zzakD = i;
    }

    private static Status zza(RemoteException remoteException) {
        StringBuilder stringBuilder = new StringBuilder();
        if (zzt.zzzh() && (remoteException instanceof TransactionTooLargeException)) {
            stringBuilder.append("TransactionTooLargeException: ");
        }
        stringBuilder.append(remoteException.getLocalizedMessage());
        return new Status(8, stringBuilder.toString());
    }

    public abstract void zza(@NonNull zzaal com_google_android_gms_internal_zzaal, boolean z);

    public abstract void zza(com.google.android.gms.internal.zzaax.zza<?> com_google_android_gms_internal_zzaax_zza_) throws DeadObjectException;

    public abstract void zzz(@NonNull Status status);
}
