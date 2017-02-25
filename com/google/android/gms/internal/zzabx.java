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

public class zzabx<R extends Result> extends TransformedResult<R> implements ResultCallback<R> {
    private final Object zzaAh = new Object();
    private final WeakReference<GoogleApiClient> zzaAj;
    private ResultTransform<? super R, ? extends Result> zzaDl = null;
    private zzabx<? extends Result> zzaDm = null;
    private volatile ResultCallbacks<? super R> zzaDn = null;
    private PendingResult<R> zzaDo = null;
    private Status zzaDp = null;
    private final zza zzaDq;
    private boolean zzaDr = false;

    private final class zza extends Handler {
        final /* synthetic */ zzabx zzaDt;

        public zza(zzabx com_google_android_gms_internal_zzabx, Looper looper) {
            this.zzaDt = com_google_android_gms_internal_zzabx;
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    PendingResult pendingResult = (PendingResult) message.obj;
                    synchronized (this.zzaDt.zzaAh) {
                        if (pendingResult == null) {
                            this.zzaDt.zzaDm.zzE(new Status(13, "Transform returned null"));
                        } else if (pendingResult instanceof zzabp) {
                            this.zzaDt.zzaDm.zzE(((zzabp) pendingResult).getStatus());
                        } else {
                            this.zzaDt.zzaDm.zza(pendingResult);
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

    public zzabx(WeakReference<GoogleApiClient> weakReference) {
        zzac.zzb((Object) weakReference, (Object) "GoogleApiClient reference must not be null");
        this.zzaAj = weakReference;
        GoogleApiClient googleApiClient = (GoogleApiClient) this.zzaAj.get();
        this.zzaDq = new zza(this, googleApiClient != null ? googleApiClient.getLooper() : Looper.getMainLooper());
    }

    private void zzE(Status status) {
        synchronized (this.zzaAh) {
            this.zzaDp = status;
            zzF(this.zzaDp);
        }
    }

    private void zzF(Status status) {
        synchronized (this.zzaAh) {
            if (this.zzaDl != null) {
                Object onFailure = this.zzaDl.onFailure(status);
                zzac.zzb(onFailure, (Object) "onFailure must not return null");
                this.zzaDm.zzE(onFailure);
            } else if (zzxc()) {
                this.zzaDn.onFailure(status);
            }
        }
    }

    private void zzd(Result result) {
        if (result instanceof Releasable) {
            try {
                ((Releasable) result).release();
            } catch (Throwable e) {
                String valueOf = String.valueOf(result);
                Log.w("TransformedResultImpl", new StringBuilder(String.valueOf(valueOf).length() + 18).append("Unable to release ").append(valueOf).toString(), e);
            }
        }
    }

    private void zzxa() {
        if (this.zzaDl != null || this.zzaDn != null) {
            GoogleApiClient googleApiClient = (GoogleApiClient) this.zzaAj.get();
            if (!(this.zzaDr || this.zzaDl == null || googleApiClient == null)) {
                googleApiClient.zza(this);
                this.zzaDr = true;
            }
            if (this.zzaDp != null) {
                zzF(this.zzaDp);
            } else if (this.zzaDo != null) {
                this.zzaDo.setResultCallback(this);
            }
        }
    }

    private boolean zzxc() {
        return (this.zzaDn == null || ((GoogleApiClient) this.zzaAj.get()) == null) ? false : true;
    }

    public void andFinally(@NonNull ResultCallbacks<? super R> resultCallbacks) {
        boolean z = true;
        synchronized (this.zzaAh) {
            zzac.zza(this.zzaDn == null, (Object) "Cannot call andFinally() twice.");
            if (this.zzaDl != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zzaDn = resultCallbacks;
            zzxa();
        }
    }

    public void onResult(final R r) {
        synchronized (this.zzaAh) {
            if (!r.getStatus().isSuccess()) {
                zzE(r.getStatus());
                zzd((Result) r);
            } else if (this.zzaDl != null) {
                zzabo.zzwv().submit(new Runnable(this) {
                    final /* synthetic */ zzabx zzaDt;

                    @WorkerThread
                    public void run() {
                        GoogleApiClient googleApiClient;
                        try {
                            zzaaf.zzaAg.set(Boolean.valueOf(true));
                            this.zzaDt.zzaDq.sendMessage(this.zzaDt.zzaDq.obtainMessage(0, this.zzaDt.zzaDl.onSuccess(r)));
                            zzaaf.zzaAg.set(Boolean.valueOf(false));
                            this.zzaDt.zzd(r);
                            googleApiClient = (GoogleApiClient) this.zzaDt.zzaAj.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zzaDt);
                            }
                        } catch (RuntimeException e) {
                            this.zzaDt.zzaDq.sendMessage(this.zzaDt.zzaDq.obtainMessage(1, e));
                            zzaaf.zzaAg.set(Boolean.valueOf(false));
                            this.zzaDt.zzd(r);
                            googleApiClient = (GoogleApiClient) this.zzaDt.zzaAj.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zzaDt);
                            }
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            zzaaf.zzaAg.set(Boolean.valueOf(false));
                            this.zzaDt.zzd(r);
                            googleApiClient = (GoogleApiClient) this.zzaDt.zzaAj.get();
                            if (googleApiClient != null) {
                                googleApiClient.zzb(this.zzaDt);
                            }
                        }
                    }
                });
            } else if (zzxc()) {
                this.zzaDn.onSuccess(r);
            }
        }
    }

    @NonNull
    public <S extends Result> TransformedResult<S> then(@NonNull ResultTransform<? super R, ? extends S> resultTransform) {
        TransformedResult com_google_android_gms_internal_zzabx;
        boolean z = true;
        synchronized (this.zzaAh) {
            zzac.zza(this.zzaDl == null, (Object) "Cannot call then() twice.");
            if (this.zzaDn != null) {
                z = false;
            }
            zzac.zza(z, (Object) "Cannot call then() and andFinally() on the same TransformedResult.");
            this.zzaDl = resultTransform;
            com_google_android_gms_internal_zzabx = new zzabx(this.zzaAj);
            this.zzaDm = com_google_android_gms_internal_zzabx;
            zzxa();
        }
        return com_google_android_gms_internal_zzabx;
    }

    public void zza(PendingResult<?> pendingResult) {
        synchronized (this.zzaAh) {
            this.zzaDo = pendingResult;
            zzxa();
        }
    }

    void zzxb() {
        this.zzaDn = null;
    }
}
