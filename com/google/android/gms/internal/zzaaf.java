package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzaaf<R extends Result> extends PendingResult<R> {
    static final ThreadLocal<Boolean> zzaAg = new ThreadLocal<Boolean>() {
        protected /* synthetic */ Object initialValue() {
            return zzvJ();
        }

        protected Boolean zzvJ() {
            return Boolean.valueOf(false);
        }
    };
    private boolean zzK;
    private final Object zzaAh;
    protected final zza<R> zzaAi;
    protected final WeakReference<GoogleApiClient> zzaAj;
    private final ArrayList<com.google.android.gms.common.api.PendingResult.zza> zzaAk;
    private ResultCallback<? super R> zzaAl;
    private final AtomicReference<zzb> zzaAm;
    private zzb zzaAn;
    private volatile boolean zzaAo;
    private boolean zzaAp;
    private zzs zzaAq;
    private volatile zzabx<R> zzaAr;
    private boolean zzaAs;
    private Status zzair;
    private R zzazt;
    private final CountDownLatch zztj;

    public static class zza<R extends Result> extends Handler {
        public zza() {
            this(Looper.getMainLooper());
        }

        public zza(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Pair pair = (Pair) message.obj;
                    zzb((ResultCallback) pair.first, (Result) pair.second);
                    return;
                case 2:
                    ((zzaaf) message.obj).zzC(Status.zzazA);
                    return;
                default:
                    Log.wtf("BasePendingResult", "Don't know how to handle message: " + message.what, new Exception());
                    return;
            }
        }

        public void zza(ResultCallback<? super R> resultCallback, R r) {
            sendMessage(obtainMessage(1, new Pair(resultCallback, r)));
        }

        public void zza(zzaaf<R> com_google_android_gms_internal_zzaaf_R, long j) {
            sendMessageDelayed(obtainMessage(2, com_google_android_gms_internal_zzaaf_R), j);
        }

        protected void zzb(ResultCallback<? super R> resultCallback, R r) {
            try {
                resultCallback.onResult(r);
            } catch (RuntimeException e) {
                zzaaf.zzd(r);
                throw e;
            }
        }

        public void zzvK() {
            removeMessages(2);
        }
    }

    private final class zzb {
        final /* synthetic */ zzaaf zzaAt;

        private zzb(zzaaf com_google_android_gms_internal_zzaaf) {
            this.zzaAt = com_google_android_gms_internal_zzaaf;
        }

        protected void finalize() throws Throwable {
            zzaaf.zzd(this.zzaAt.zzazt);
            super.finalize();
        }
    }

    @Deprecated
    zzaaf() {
        this.zzaAh = new Object();
        this.zztj = new CountDownLatch(1);
        this.zzaAk = new ArrayList();
        this.zzaAm = new AtomicReference();
        this.zzaAs = false;
        this.zzaAi = new zza(Looper.getMainLooper());
        this.zzaAj = new WeakReference(null);
    }

    @Deprecated
    protected zzaaf(Looper looper) {
        this.zzaAh = new Object();
        this.zztj = new CountDownLatch(1);
        this.zzaAk = new ArrayList();
        this.zzaAm = new AtomicReference();
        this.zzaAs = false;
        this.zzaAi = new zza(looper);
        this.zzaAj = new WeakReference(null);
    }

    protected zzaaf(GoogleApiClient googleApiClient) {
        this.zzaAh = new Object();
        this.zztj = new CountDownLatch(1);
        this.zzaAk = new ArrayList();
        this.zzaAm = new AtomicReference();
        this.zzaAs = false;
        this.zzaAi = new zza(googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
        this.zzaAj = new WeakReference(googleApiClient);
    }

    private R get() {
        R r;
        boolean z = true;
        synchronized (this.zzaAh) {
            if (this.zzaAo) {
                z = false;
            }
            zzac.zza(z, (Object) "Result has already been consumed.");
            zzac.zza(isReady(), (Object) "Result is not ready.");
            r = this.zzazt;
            this.zzazt = null;
            this.zzaAl = null;
            this.zzaAo = true;
        }
        zzvG();
        return r;
    }

    private void zzc(R r) {
        this.zzazt = r;
        this.zzaAq = null;
        this.zztj.countDown();
        this.zzair = this.zzazt.getStatus();
        if (this.zzK) {
            this.zzaAl = null;
        } else if (this.zzaAl != null) {
            this.zzaAi.zzvK();
            this.zzaAi.zza(this.zzaAl, get());
        } else if (this.zzazt instanceof Releasable) {
            this.zzaAn = new zzb();
        }
        Iterator it = this.zzaAk.iterator();
        while (it.hasNext()) {
            ((com.google.android.gms.common.api.PendingResult.zza) it.next()).zzy(this.zzair);
        }
        this.zzaAk.clear();
    }

    public static void zzd(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("BasePendingResult", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    private void zzvG() {
        zzb com_google_android_gms_internal_zzaby_zzb = (zzb) this.zzaAm.getAndSet(null);
        if (com_google_android_gms_internal_zzaby_zzb != null) {
            com_google_android_gms_internal_zzaby_zzb.zzc(this);
        }
    }

    public final R await() {
        boolean z = true;
        zzac.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "await must not be called on the UI thread");
        zzac.zza(!this.zzaAo, (Object) "Result has already been consumed");
        if (this.zzaAr != null) {
            z = false;
        }
        zzac.zza(z, (Object) "Cannot await if then() has been called.");
        try {
            this.zztj.await();
        } catch (InterruptedException e) {
            zzC(Status.zzazy);
        }
        zzac.zza(isReady(), (Object) "Result is not ready.");
        return get();
    }

    public final R await(long j, TimeUnit timeUnit) {
        boolean z = true;
        boolean z2 = j <= 0 || Looper.myLooper() != Looper.getMainLooper();
        zzac.zza(z2, (Object) "await must not be called on the UI thread when time is greater than zero.");
        zzac.zza(!this.zzaAo, (Object) "Result has already been consumed.");
        if (this.zzaAr != null) {
            z = false;
        }
        zzac.zza(z, (Object) "Cannot await if then() has been called.");
        try {
            if (!this.zztj.await(j, timeUnit)) {
                zzC(Status.zzazA);
            }
        } catch (InterruptedException e) {
            zzC(Status.zzazy);
        }
        zzac.zza(isReady(), (Object) "Result is not ready.");
        return get();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancel() {
        synchronized (this.zzaAh) {
            if (this.zzK || this.zzaAo) {
            } else {
                if (this.zzaAq != null) {
                    try {
                        this.zzaAq.cancel();
                    } catch (RemoteException e) {
                    }
                }
                zzd(this.zzazt);
                this.zzK = true;
                zzc(zzc(Status.zzazB));
            }
        }
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this.zzaAh) {
            z = this.zzK;
        }
        return z;
    }

    public final boolean isReady() {
        return this.zztj.getCount() == 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setResultCallback(ResultCallback<? super R> resultCallback) {
        boolean z = true;
        synchronized (this.zzaAh) {
            if (resultCallback == null) {
                this.zzaAl = null;
                return;
            }
            zzac.zza(!this.zzaAo, (Object) "Result has already been consumed.");
            if (this.zzaAr != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
            } else if (isReady()) {
                this.zzaAi.zza((ResultCallback) resultCallback, get());
            } else {
                this.zzaAl = resultCallback;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setResultCallback(ResultCallback<? super R> resultCallback, long j, TimeUnit timeUnit) {
        boolean z = true;
        synchronized (this.zzaAh) {
            if (resultCallback == null) {
                this.zzaAl = null;
                return;
            }
            zzac.zza(!this.zzaAo, (Object) "Result has already been consumed.");
            if (this.zzaAr != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
            } else if (isReady()) {
                this.zzaAi.zza((ResultCallback) resultCallback, get());
            } else {
                this.zzaAl = resultCallback;
                this.zzaAi.zza(this, timeUnit.toMillis(j));
            }
        }
    }

    public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult<S> then;
        boolean z = true;
        zzac.zza(!this.zzaAo, (Object) "Result has already been consumed.");
        synchronized (this.zzaAh) {
            zzac.zza(this.zzaAr == null, (Object) "Cannot call then() twice.");
            zzac.zza(this.zzaAl == null, (Object) "Cannot call then() if callbacks are set.");
            if (this.zzK) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() if result was canceled.");
            this.zzaAs = true;
            this.zzaAr = new zzabx(this.zzaAj);
            then = this.zzaAr.then(resultTransform);
            if (isReady()) {
                this.zzaAi.zza(this.zzaAr, get());
            } else {
                this.zzaAl = this.zzaAr;
            }
        }
        return then;
    }

    public final void zzC(Status status) {
        synchronized (this.zzaAh) {
            if (!isReady()) {
                zzb(zzc(status));
                this.zzaAp = true;
            }
        }
    }

    public final void zza(com.google.android.gms.common.api.PendingResult.zza com_google_android_gms_common_api_PendingResult_zza) {
        zzac.zzb(com_google_android_gms_common_api_PendingResult_zza != null, (Object) "Callback cannot be null.");
        synchronized (this.zzaAh) {
            if (isReady()) {
                com_google_android_gms_common_api_PendingResult_zza.zzy(this.zzair);
            } else {
                this.zzaAk.add(com_google_android_gms_common_api_PendingResult_zza);
            }
        }
    }

    protected final void zza(zzs com_google_android_gms_common_internal_zzs) {
        synchronized (this.zzaAh) {
            this.zzaAq = com_google_android_gms_common_internal_zzs;
        }
    }

    public void zza(zzb com_google_android_gms_internal_zzaby_zzb) {
        this.zzaAm.set(com_google_android_gms_internal_zzaby_zzb);
    }

    public final void zzb(R r) {
        boolean z = true;
        synchronized (this.zzaAh) {
            if (this.zzaAp || this.zzK) {
                zzd(r);
                return;
            }
            if (isReady()) {
            }
            zzac.zza(!isReady(), (Object) "Results have already been set");
            if (this.zzaAo) {
                z = false;
            }
            zzac.zza(z, (Object) "Result has already been consumed");
            zzc((Result) r);
        }
    }

    @NonNull
    protected abstract R zzc(Status status);

    public boolean zzvF() {
        boolean isCanceled;
        synchronized (this.zzaAh) {
            if (((GoogleApiClient) this.zzaAj.get()) == null || !this.zzaAs) {
                cancel();
            }
            isCanceled = isCanceled();
        }
        return isCanceled;
    }

    public void zzvH() {
        setResultCallback(null);
    }

    public void zzvI() {
        boolean z = this.zzaAs || ((Boolean) zzaAg.get()).booleanValue();
        this.zzaAs = z;
    }

    public Integer zzvr() {
        return null;
    }
}
