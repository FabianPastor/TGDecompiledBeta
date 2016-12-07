package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.TransformedResult;
import com.google.android.gms.common.internal.zzaa;
import java.lang.ref.WeakReference;

public class zzsf<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private final zza BA;
    private boolean BB = false;
    private ResultTransform<? super R, ? extends Result> Bv = null;
    private zzsf<? extends Result> Bw = null;
    private volatile ResultCallbacks<? super R> Bx = null;
    private PendingResult<R> By = null;
    private Status Bz = null;
    private final Object yH = new Object();
    private final WeakReference<GoogleApiClient> yJ;

    private final class zza extends Handler {
        final /* synthetic */ zzsf BD;

        public zza(zzsf com_google_android_gms_internal_zzsf, Looper looper) {
            this.BD = com_google_android_gms_internal_zzsf;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    PendingResult pendingResult = (PendingResult) message.obj;
                    synchronized (this.BD.yH) {
                        if (pendingResult == null) {
                            this.BD.Bw.zzad(new Status(13, "Transform returned null"));
                        } else if (pendingResult instanceof zzrz) {
                            this.BD.Bw.zzad(((zzrz) pendingResult).getStatus());
                        } else {
                            this.BD.Bw.zza(pendingResult);
                        }
                    }
                    return;
                case 1:
                    RuntimeException runtimeException = (RuntimeException) message.obj;
                    String str = "TransformedResultImpl";
                    String str2 = "Runtime exception on the transformation worker thread: ";
                    String valueOf = String.valueOf(runtimeException.getMessage());
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    throw runtimeException;
                default:
                    Log.e("TransformedResultImpl", "TransformationResultHandler received unknown message type: " + message.what);
                    return;
            }
        }
    }

    public zzsf(WeakReference<GoogleApiClient> weakReference) {
        zzaa.zzb((Object) weakReference, (Object) "GoogleApiClient reference must not be null");
        this.yJ = weakReference;
        GoogleApiClient googleApiClient = (GoogleApiClient) this.yJ.get();
        this.BA = new zza(this, googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
    }

    private void zzad(Status status) {
        synchronized (this.yH) {
            this.Bz = status;
            zzae(this.Bz);
        }
    }

    private void zzae(Status status) {
        synchronized (this.yH) {
            if (this.Bv != null) {
                Object onFailure = this.Bv.onFailure(status);
                zzaa.zzb(onFailure, (Object) "onFailure must not return null");
                this.Bw.zzad(onFailure);
            } else if (zzaue()) {
                this.Bx.onFailure(status);
            }
        }
    }

    private void zzauc() {
        if (this.Bv != null || this.Bx != null) {
            GoogleApiClient googleApiClient = (GoogleApiClient) this.yJ.get();
            if (!(this.BB || this.Bv == null || googleApiClient == null)) {
                googleApiClient.zza(this);
                this.BB = true;
            }
            if (this.Bz != null) {
                zzae(this.Bz);
            } else if (this.By != null) {
                this.By.setResultCallback(this);
            }
        }
    }

    private boolean zzaue() {
        return (this.Bx == null || ((GoogleApiClient) this.yJ.get()) == null) ? false : true;
    }

    private void zze(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("TransformedResultImpl", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    public void andFinally(@NonNull ResultCallbacks<? super R> resultCallbacks) {
        boolean z = true;
        synchronized (this.yH) {
            zzaa.zza(this.Bx == null, (Object) "Cannot call andFinally() twice.");
            if (this.Bv != null) {
                z = false;
            }
            zzaa.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.Bx = resultCallbacks;
            zzauc();
        }
    }

    public void onResult(final R r) {
        synchronized (this.yH) {
            if (!r.getStatus().isSuccess()) {
                zzad(r.getStatus());
                zze((Result) r);
            } else if (this.Bv != null) {
                zzry.zzatf().submit(new Runnable(this) {
                    final /* synthetic */ zzsf BD;

                    @WorkerThread
                    public void run() {
                        GoogleApiClient googleApiClient;
                        try {
                            zzqq.yG.set(Boolean.valueOf(true));
                            this.BD.BA.sendMessage(this.BD.BA.obtainMessage(0, this.BD.Bv.onSuccess(r)));
                            zzqq.yG.set(Boolean.valueOf(false));
                            this.BD.zze(r);
                            googleApiClient = (GoogleApiClient) this.BD.yJ.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.BD);
                            }
                        } catch (RuntimeException e) {
                            this.BD.BA.sendMessage(this.BD.BA.obtainMessage(1, e));
                            zzqq.yG.set(Boolean.valueOf(false));
                            this.BD.zze(r);
                            googleApiClient = (GoogleApiClient) this.BD.yJ.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.BD);
                            }
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            zzqq.yG.set(Boolean.valueOf(false));
                            this.BD.zze(r);
                            googleApiClient = (GoogleApiClient) this.BD.yJ.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.BD);
                            }
                        }
                    }
                });
            } else if (zzaue()) {
                this.Bx.onSuccess(r);
            }
        }
    }

    @NonNull
    public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult com_google_android_gms_internal_zzsf;
        boolean z = true;
        synchronized (this.yH) {
            zzaa.zza(this.Bv == null, (Object) "Cannot call then() twice.");
            if (this.Bx != null) {
                z = false;
            }
            zzaa.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.Bv = resultTransform;
            com_google_android_gms_internal_zzsf = new zzsf(this.yJ);
            this.Bw = com_google_android_gms_internal_zzsf;
            zzauc();
        }
        return com_google_android_gms_internal_zzsf;
    }

    public void zza(PendingResult<?> pendingResult) {
        synchronized (this.yH) {
            this.By = pendingResult;
            zzauc();
        }
    }

    void zzaud() {
        this.Bx = null;
    }
}
