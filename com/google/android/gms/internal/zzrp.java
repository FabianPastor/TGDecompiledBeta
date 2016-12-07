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
import com.google.android.gms.common.internal.zzac;
import java.lang.ref.WeakReference;

public class zzrp<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private final Object wG = new Object();
    private final WeakReference<GoogleApiClient> wI;
    private ResultTransform<? super R, ? extends Result> zk = null;
    private zzrp<? extends Result> zl = null;
    private volatile ResultCallbacks<? super R> zm = null;
    private PendingResult<R> zn = null;
    private Status zo = null;
    private final zza zp;
    private boolean zq = false;

    private final class zza extends Handler {
        final /* synthetic */ zzrp zs;

        public zza(zzrp com_google_android_gms_internal_zzrp, Looper looper) {
            this.zs = com_google_android_gms_internal_zzrp;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    PendingResult pendingResult = (PendingResult) message.obj;
                    synchronized (this.zs.wG) {
                        if (pendingResult == null) {
                            this.zs.zl.zzac(new Status(13, "Transform returned null"));
                        } else if (pendingResult instanceof zzrk) {
                            this.zs.zl.zzac(((zzrk) pendingResult).getStatus());
                        } else {
                            this.zs.zl.zza(pendingResult);
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

    public zzrp(WeakReference<GoogleApiClient> weakReference) {
        zzac.zzb((Object) weakReference, (Object) "GoogleApiClient reference must not be null");
        this.wI = weakReference;
        GoogleApiClient googleApiClient = (GoogleApiClient) this.wI.get();
        this.zp = new zza(this, googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
    }

    private void zzac(Status status) {
        synchronized (this.wG) {
            this.zo = status;
            zzad(this.zo);
        }
    }

    private void zzad(Status status) {
        synchronized (this.wG) {
            if (this.zk != null) {
                Object onFailure = this.zk.onFailure(status);
                zzac.zzb(onFailure, (Object) "onFailure must not return null");
                this.zl.zzac(onFailure);
            } else if (zzasv()) {
                this.zm.onFailure(status);
            }
        }
    }

    private void zzast() {
        if (this.zk != null || this.zm != null) {
            GoogleApiClient googleApiClient = (GoogleApiClient) this.wI.get();
            if (!(this.zq || this.zk == null || googleApiClient == null)) {
                googleApiClient.zza(this);
                this.zq = true;
            }
            if (this.zo != null) {
                zzad(this.zo);
            } else if (this.zn != null) {
                this.zn.setResultCallback(this);
            }
        }
    }

    private boolean zzasv() {
        return (this.zm == null || ((GoogleApiClient) this.wI.get()) == null) ? false : true;
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
        synchronized (this.wG) {
            zzac.zza(this.zm == null, (Object) "Cannot call andFinally() twice.");
            if (this.zk != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zm = resultCallbacks;
            zzast();
        }
    }

    public void onResult(final R r) {
        synchronized (this.wG) {
            if (!r.getStatus().isSuccess()) {
                zzac(r.getStatus());
                zze((Result) r);
            } else if (this.zk != null) {
                zzrj.zzarz().submit(new Runnable(this) {
                    final /* synthetic */ zzrp zs;

                    @WorkerThread
                    public void run() {
                        GoogleApiClient googleApiClient;
                        try {
                            zzqe.wF.set(Boolean.valueOf(true));
                            this.zs.zp.sendMessage(this.zs.zp.obtainMessage(0, this.zs.zk.onSuccess(r)));
                            zzqe.wF.set(Boolean.valueOf(false));
                            this.zs.zze(r);
                            googleApiClient = (GoogleApiClient) this.zs.wI.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zs);
                            }
                        } catch (RuntimeException e) {
                            this.zs.zp.sendMessage(this.zs.zp.obtainMessage(1, e));
                            zzqe.wF.set(Boolean.valueOf(false));
                            this.zs.zze(r);
                            googleApiClient = (GoogleApiClient) this.zs.wI.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zs);
                            }
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            zzqe.wF.set(Boolean.valueOf(false));
                            this.zs.zze(r);
                            googleApiClient = (GoogleApiClient) this.zs.wI.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zs);
                            }
                        }
                    }
                });
            } else if (zzasv()) {
                this.zm.onSuccess(r);
            }
        }
    }

    @NonNull
    public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult com_google_android_gms_internal_zzrp;
        boolean z = true;
        synchronized (this.wG) {
            zzac.zza(this.zk == null, (Object) "Cannot call then() twice.");
            if (this.zm != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zk = resultTransform;
            com_google_android_gms_internal_zzrp = new zzrp(this.wI);
            this.zl = com_google_android_gms_internal_zzrp;
            zzast();
        }
        return com_google_android_gms_internal_zzrp;
    }

    public void zza(PendingResult<?> pendingResult) {
        synchronized (this.wG) {
            this.zn = pendingResult;
            zzast();
        }
    }

    void zzasu() {
        this.zm = null;
    }
}
