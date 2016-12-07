package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzq;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzqq<R extends Result> extends PendingResult<R> {
    static final ThreadLocal<Boolean> yG = new ThreadLocal<Boolean>() {
        protected /* synthetic */ Object initialValue() {
            return zzarw();
        }

        protected Boolean zzarw() {
            return Boolean.valueOf(false);
        }
    };
    private R xV;
    private final Object yH;
    protected final zza<R> yI;
    protected final WeakReference<GoogleApiClient> yJ;
    private final ArrayList<com.google.android.gms.common.api.PendingResult.zza> yK;
    private ResultCallback<? super R> yL;
    private final AtomicReference<zzb> yM;
    private zzb yN;
    private volatile boolean yO;
    private boolean yP;
    private zzq yQ;
    private volatile zzsf<R> yR;
    private boolean yS;
    private boolean zzak;
    private final CountDownLatch zzank;

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
                    ((zzqq) message.obj).zzab(Status.yc);
                    return;
                default:
                    Log.wtf("BasePendingResult", "Don't know how to handle message: " + message.what, new Exception());
                    return;
            }
        }

        public void zza(ResultCallback<? super R> resultCallback, R r) {
            sendMessage(obtainMessage(1, new Pair(resultCallback, r)));
        }

        public void zza(zzqq<R> com_google_android_gms_internal_zzqq_R, long j) {
            sendMessageDelayed(obtainMessage(2, com_google_android_gms_internal_zzqq_R), j);
        }

        public void zzarx() {
            removeMessages(2);
        }

        protected void zzb(ResultCallback<? super R> resultCallback, R r) {
            try {
                resultCallback.onResult(r);
            } catch (RuntimeException e) {
                zzqq.zze(r);
                throw e;
            }
        }
    }

    private final class zzb {
        final /* synthetic */ zzqq yT;

        private zzb(zzqq com_google_android_gms_internal_zzqq) {
            this.yT = com_google_android_gms_internal_zzqq;
        }

        protected void finalize() throws Throwable {
            zzqq.zze(this.yT.xV);
            super.finalize();
        }
    }

    @Deprecated
    zzqq() {
        this.yH = new Object();
        this.zzank = new CountDownLatch(1);
        this.yK = new ArrayList();
        this.yM = new AtomicReference();
        this.yS = false;
        this.yI = new zza(Looper.getMainLooper());
        this.yJ = new WeakReference(null);
    }

    @Deprecated
    protected zzqq(Looper looper) {
        this.yH = new Object();
        this.zzank = new CountDownLatch(1);
        this.yK = new ArrayList();
        this.yM = new AtomicReference();
        this.yS = false;
        this.yI = new zza(looper);
        this.yJ = new WeakReference(null);
    }

    protected zzqq(GoogleApiClient googleApiClient) {
        this.yH = new Object();
        this.zzank = new CountDownLatch(1);
        this.yK = new ArrayList();
        this.yM = new AtomicReference();
        this.yS = false;
        this.yI = new zza(googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
        this.yJ = new WeakReference(googleApiClient);
    }

    private R get() {
        R r;
        boolean z = true;
        synchronized (this.yH) {
            if (this.yO) {
                z = false;
            }
            zzaa.zza(z, (Object) "Result has already been consumed.");
            zzaa.zza(isReady(), (Object) "Result is not ready.");
            r = this.xV;
            this.xV = null;
            this.yL = null;
            this.yO = true;
        }
        zzart();
        return r;
    }

    private void zzart() {
        zzb com_google_android_gms_internal_zzsg_zzb = (zzb) this.yM.getAndSet(null);
        if (com_google_android_gms_internal_zzsg_zzb != null) {
            com_google_android_gms_internal_zzsg_zzb.zzc(this);
        }
    }

    private void zzd(R r) {
        this.xV = r;
        this.yQ = null;
        this.zzank.countDown();
        Status status = this.xV.getStatus();
        if (this.zzak) {
            this.yL = null;
        } else if (this.yL != null) {
            this.yI.zzarx();
            this.yI.zza(this.yL, get());
        } else if (this.xV instanceof Releasable) {
            this.yN = new zzb();
        }
        Iterator it = this.yK.iterator();
        while (it.hasNext()) {
            ((com.google.android.gms.common.api.PendingResult.zza) it.next()).zzx(status);
        }
        this.yK.clear();
    }

    public static void zze(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("BasePendingResult", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    public final R await() {
        boolean z = true;
        zzaa.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "await must not be called on the UI thread");
        zzaa.zza(!this.yO, (Object) "Result has already been consumed");
        if (this.yR != null) {
            z = false;
        }
        zzaa.zza(z, (Object) "Cannot await if then() has been called.");
        try {
            this.zzank.await();
        } catch (InterruptedException e) {
            zzab(Status.ya);
        }
        zzaa.zza(isReady(), (Object) "Result is not ready.");
        return get();
    }

    public final R await(long j, TimeUnit timeUnit) {
        boolean z = true;
        boolean z2 = j <= 0 || Looper.myLooper() != Looper.getMainLooper();
        zzaa.zza(z2, (Object) "await must not be called on the UI thread when time is greater than zero.");
        zzaa.zza(!this.yO, (Object) "Result has already been consumed.");
        if (this.yR != null) {
            z = false;
        }
        zzaa.zza(z, (Object) "Cannot await if then() has been called.");
        try {
            if (!this.zzank.await(j, timeUnit)) {
                zzab(Status.yc);
            }
        } catch (InterruptedException e) {
            zzab(Status.ya);
        }
        zzaa.zza(isReady(), (Object) "Result is not ready.");
        return get();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancel() {
        synchronized (this.yH) {
            if (this.zzak || this.yO) {
            } else {
                if (this.yQ != null) {
                    try {
                        this.yQ.cancel();
                    } catch (RemoteException e) {
                    }
                }
                zze(this.xV);
                this.zzak = true;
                zzd(zzc(Status.yd));
            }
        }
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this.yH) {
            z = this.zzak;
        }
        return z;
    }

    public final boolean isReady() {
        return this.zzank.getCount() == 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setResultCallback(ResultCallback<? super R> resultCallback) {
        boolean z = true;
        synchronized (this.yH) {
            if (resultCallback == null) {
                this.yL = null;
                return;
            }
            zzaa.zza(!this.yO, (Object) "Result has already been consumed.");
            if (this.yR != null) {
                z = false;
            }
            zzaa.zza(z, (Object) "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
            } else if (isReady()) {
                this.yI.zza((ResultCallback) resultCallback, get());
            } else {
                this.yL = resultCallback;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setResultCallback(ResultCallback<? super R> resultCallback, long j, TimeUnit timeUnit) {
        boolean z = true;
        synchronized (this.yH) {
            if (resultCallback == null) {
                this.yL = null;
                return;
            }
            zzaa.zza(!this.yO, (Object) "Result has already been consumed.");
            if (this.yR != null) {
                z = false;
            }
            zzaa.zza(z, (Object) "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
            } else if (isReady()) {
                this.yI.zza((ResultCallback) resultCallback, get());
            } else {
                this.yL = resultCallback;
                this.yI.zza(this, timeUnit.toMillis(j));
            }
        }
    }

    public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult<S> then;
        boolean z = true;
        zzaa.zza(!this.yO, (Object) "Result has already been consumed.");
        synchronized (this.yH) {
            zzaa.zza(this.yR == null, (Object) "Cannot call then() twice.");
            if (this.yL != null) {
                z = false;
            }
            zzaa.zza(z, (Object) "Cannot call then() if callbacks are set.");
            this.yS = true;
            this.yR = new zzsf(this.yJ);
            then = this.yR.then(resultTransform);
            if (isReady()) {
                this.yI.zza(this.yR, get());
            } else {
                this.yL = this.yR;
            }
        }
        return then;
    }

    public final void zza(com.google.android.gms.common.api.PendingResult.zza com_google_android_gms_common_api_PendingResult_zza) {
        boolean z = true;
        zzaa.zza(!this.yO, (Object) "Result has already been consumed.");
        if (com_google_android_gms_common_api_PendingResult_zza == null) {
            z = false;
        }
        zzaa.zzb(z, (Object) "Callback cannot be null.");
        synchronized (this.yH) {
            if (isReady()) {
                com_google_android_gms_common_api_PendingResult_zza.zzx(this.xV.getStatus());
            } else {
                this.yK.add(com_google_android_gms_common_api_PendingResult_zza);
            }
        }
    }

    protected final void zza(zzq com_google_android_gms_common_internal_zzq) {
        synchronized (this.yH) {
            this.yQ = com_google_android_gms_common_internal_zzq;
        }
    }

    public void zza(zzb com_google_android_gms_internal_zzsg_zzb) {
        this.yM.set(com_google_android_gms_internal_zzsg_zzb);
    }

    public final void zzab(Status status) {
        synchronized (this.yH) {
            if (!isReady()) {
                zzc(zzc(status));
                this.yP = true;
            }
        }
    }

    public Integer zzarh() {
        return null;
    }

    public boolean zzars() {
        boolean isCanceled;
        synchronized (this.yH) {
            if (((GoogleApiClient) this.yJ.get()) == null || !this.yS) {
                cancel();
            }
            isCanceled = isCanceled();
        }
        return isCanceled;
    }

    public void zzaru() {
        setResultCallback(null);
    }

    public void zzarv() {
        boolean z = this.yS || ((Boolean) yG.get()).booleanValue();
        this.yS = z;
    }

    protected abstract R zzc(Status status);

    public final void zzc(R r) {
        boolean z = true;
        synchronized (this.yH) {
            if (this.yP || this.zzak) {
                zze(r);
                return;
            }
            if (isReady()) {
            }
            zzaa.zza(!isReady(), (Object) "Results have already been set");
            if (this.yO) {
                z = false;
            }
            zzaa.zza(z, (Object) "Result has already been consumed");
            zzd(r);
        }
    }
}
