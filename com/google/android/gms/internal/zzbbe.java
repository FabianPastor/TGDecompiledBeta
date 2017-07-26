package com.google.android.gms.internal;

import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzao;
import com.google.android.gms.common.internal.zzbo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class zzbbe<R extends Result> extends PendingResult<R> {
    static final ThreadLocal<Boolean> zzaBV = new zzbbf();
    private Status mStatus;
    private boolean zzJ;
    private final Object zzaBW;
    private zzbbg<R> zzaBX;
    private WeakReference<GoogleApiClient> zzaBY;
    private final ArrayList<zza> zzaBZ;
    private R zzaBj;
    private ResultCallback<? super R> zzaCa;
    private final AtomicReference<zzbex> zzaCb;
    private zzbbh zzaCc;
    private volatile boolean zzaCd;
    private boolean zzaCe;
    private zzao zzaCf;
    private volatile zzbes<R> zzaCg;
    private boolean zzaCh;
    private final CountDownLatch zztJ;

    @Deprecated
    zzbbe() {
        this.zzaBW = new Object();
        this.zztJ = new CountDownLatch(1);
        this.zzaBZ = new ArrayList();
        this.zzaCb = new AtomicReference();
        this.zzaCh = false;
        this.zzaBX = new zzbbg(Looper.getMainLooper());
        this.zzaBY = new WeakReference(null);
    }

    @Deprecated
    protected zzbbe(Looper looper) {
        this.zzaBW = new Object();
        this.zztJ = new CountDownLatch(1);
        this.zzaBZ = new ArrayList();
        this.zzaCb = new AtomicReference();
        this.zzaCh = false;
        this.zzaBX = new zzbbg(looper);
        this.zzaBY = new WeakReference(null);
    }

    protected zzbbe(GoogleApiClient googleApiClient) {
        this.zzaBW = new Object();
        this.zztJ = new CountDownLatch(1);
        this.zzaBZ = new ArrayList();
        this.zzaCb = new AtomicReference();
        this.zzaCh = false;
        this.zzaBX = new zzbbg(googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
        this.zzaBY = new WeakReference(googleApiClient);
    }

    private final R get() {
        R r;
        boolean z = true;
        synchronized (this.zzaBW) {
            if (this.zzaCd) {
                z = false;
            }
            zzbo.zza(z, (Object) "Result has already been consumed.");
            zzbo.zza(isReady(), (Object) "Result is not ready.");
            r = this.zzaBj;
            this.zzaBj = null;
            this.zzaCa = null;
            this.zzaCd = true;
        }
        zzbex com_google_android_gms_internal_zzbex = (zzbex) this.zzaCb.getAndSet(null);
        if (com_google_android_gms_internal_zzbex != null) {
            com_google_android_gms_internal_zzbex.zzc(this);
        }
        return r;
    }

    private final void zzb(R r) {
        this.zzaBj = r;
        this.zzaCf = null;
        this.zztJ.countDown();
        this.mStatus = this.zzaBj.getStatus();
        if (this.zzJ) {
            this.zzaCa = null;
        } else if (this.zzaCa != null) {
            this.zzaBX.removeMessages(2);
            this.zzaBX.zza(this.zzaCa, get());
        } else if (this.zzaBj instanceof Releasable) {
            this.zzaCc = new zzbbh();
        }
        ArrayList arrayList = this.zzaBZ;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((zza) obj).zzo(this.mStatus);
        }
        this.zzaBZ.clear();
    }

    public static void zzc(Result result) {
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
        zzbo.zza(Looper.myLooper() != Looper.getMainLooper(), (Object) "await must not be called on the UI thread");
        zzbo.zza(!this.zzaCd, (Object) "Result has already been consumed");
        if (this.zzaCg != null) {
            z = false;
        }
        zzbo.zza(z, (Object) "Cannot await if then() has been called.");
        try {
            this.zztJ.await();
        } catch (InterruptedException e) {
            zzs(Status.zzaBn);
        }
        zzbo.zza(isReady(), (Object) "Result is not ready.");
        return get();
    }

    public final R await(long j, TimeUnit timeUnit) {
        boolean z = true;
        boolean z2 = j <= 0 || Looper.myLooper() != Looper.getMainLooper();
        zzbo.zza(z2, (Object) "await must not be called on the UI thread when time is greater than zero.");
        zzbo.zza(!this.zzaCd, (Object) "Result has already been consumed.");
        if (this.zzaCg != null) {
            z = false;
        }
        zzbo.zza(z, (Object) "Cannot await if then() has been called.");
        try {
            if (!this.zztJ.await(j, timeUnit)) {
                zzs(Status.zzaBp);
            }
        } catch (InterruptedException e) {
            zzs(Status.zzaBn);
        }
        zzbo.zza(isReady(), (Object) "Result is not ready.");
        return get();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancel() {
        synchronized (this.zzaBW) {
            if (this.zzJ || this.zzaCd) {
            } else {
                if (this.zzaCf != null) {
                    try {
                        this.zzaCf.cancel();
                    } catch (RemoteException e) {
                    }
                }
                zzc(this.zzaBj);
                this.zzJ = true;
                zzb(zzb(Status.zzaBq));
            }
        }
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this.zzaBW) {
            z = this.zzJ;
        }
        return z;
    }

    public final boolean isReady() {
        return this.zztJ.getCount() == 0;
    }

    public final void setResult(R r) {
        boolean z = true;
        synchronized (this.zzaBW) {
            if (this.zzaCe || this.zzJ) {
                zzc(r);
                return;
            }
            if (isReady()) {
            }
            zzbo.zza(!isReady(), (Object) "Results have already been set");
            if (this.zzaCd) {
                z = false;
            }
            zzbo.zza(z, (Object) "Result has already been consumed");
            zzb((Result) r);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setResultCallback(ResultCallback<? super R> resultCallback) {
        boolean z = true;
        synchronized (this.zzaBW) {
            if (resultCallback == null) {
                this.zzaCa = null;
                return;
            }
            zzbo.zza(!this.zzaCd, (Object) "Result has already been consumed.");
            if (this.zzaCg != null) {
                z = false;
            }
            zzbo.zza(z, (Object) "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
            } else if (isReady()) {
                this.zzaBX.zza(resultCallback, get());
            } else {
                this.zzaCa = resultCallback;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setResultCallback(ResultCallback<? super R> resultCallback, long j, TimeUnit timeUnit) {
        boolean z = true;
        synchronized (this.zzaBW) {
            if (resultCallback == null) {
                this.zzaCa = null;
                return;
            }
            zzbo.zza(!this.zzaCd, (Object) "Result has already been consumed.");
            if (this.zzaCg != null) {
                z = false;
            }
            zzbo.zza(z, (Object) "Cannot set callbacks if then() has been called.");
            if (isCanceled()) {
            } else if (isReady()) {
                this.zzaBX.zza(resultCallback, get());
            } else {
                this.zzaCa = resultCallback;
                zzbbg com_google_android_gms_internal_zzbbg = this.zzaBX;
                com_google_android_gms_internal_zzbbg.sendMessageDelayed(com_google_android_gms_internal_zzbbg.obtainMessage(2, this), timeUnit.toMillis(j));
            }
        }
    }

    public <S extends Result> TransformedResult<S> then(ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult<S> then;
        boolean z = true;
        zzbo.zza(!this.zzaCd, (Object) "Result has already been consumed.");
        synchronized (this.zzaBW) {
            zzbo.zza(this.zzaCg == null, (Object) "Cannot call then() twice.");
            zzbo.zza(this.zzaCa == null, (Object) "Cannot call then() if callbacks are set.");
            if (this.zzJ) {
                z = false;
            }
            zzbo.zza(z, (Object) "Cannot call then() if result was canceled.");
            this.zzaCh = true;
            this.zzaCg = new zzbes(this.zzaBY);
            then = this.zzaCg.then(resultTransform);
            if (isReady()) {
                this.zzaBX.zza(this.zzaCg, get());
            } else {
                this.zzaCa = this.zzaCg;
            }
        }
        return then;
    }

    public final void zza(zza com_google_android_gms_common_api_PendingResult_zza) {
        zzbo.zzb(com_google_android_gms_common_api_PendingResult_zza != null, (Object) "Callback cannot be null.");
        synchronized (this.zzaBW) {
            if (isReady()) {
                com_google_android_gms_common_api_PendingResult_zza.zzo(this.mStatus);
            } else {
                this.zzaBZ.add(com_google_android_gms_common_api_PendingResult_zza);
            }
        }
    }

    protected final void zza(zzao com_google_android_gms_common_internal_zzao) {
        synchronized (this.zzaBW) {
            this.zzaCf = com_google_android_gms_common_internal_zzao;
        }
    }

    public final void zza(zzbex com_google_android_gms_internal_zzbex) {
        this.zzaCb.set(com_google_android_gms_internal_zzbex);
    }

    @NonNull
    protected abstract R zzb(Status status);

    public final boolean zzpB() {
        boolean isCanceled;
        synchronized (this.zzaBW) {
            if (((GoogleApiClient) this.zzaBY.get()) == null || !this.zzaCh) {
                cancel();
            }
            isCanceled = isCanceled();
        }
        return isCanceled;
    }

    public final void zzpC() {
        boolean z = this.zzaCh || ((Boolean) zzaBV.get()).booleanValue();
        this.zzaCh = z;
    }

    public final Integer zzpo() {
        return null;
    }

    public final void zzs(Status status) {
        synchronized (this.zzaBW) {
            if (!isReady()) {
                setResult(zzb(status));
                this.zzaCe = true;
            }
        }
    }
}
