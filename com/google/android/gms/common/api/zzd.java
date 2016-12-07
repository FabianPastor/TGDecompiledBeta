package com.google.android.gms.common.api;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Pair;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Api.zzh;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzai;
import com.google.android.gms.internal.zzpz;
import com.google.android.gms.internal.zzqc.zza;
import com.google.android.gms.internal.zzqt;
import com.google.android.gms.internal.zzqu;
import com.google.android.gms.internal.zzre;
import com.google.android.gms.internal.zzro;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class zzd<O extends ApiOptions> {
    private final Context mContext;
    private final int mId;
    private final Api<O> tv;
    private final AtomicBoolean vA;
    private final AtomicInteger vB;
    private zze vC;
    private final zzre vv;
    private final O vw;
    private final zzpz<O> vx;
    private final zzqt vy;
    private final GoogleApiClient vz;
    private final Looper zzajn;

    public zzd(@NonNull Context context, Api<O> api, O o) {
        this(context, api, o, Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper());
    }

    public zzd(@NonNull Context context, Api<O> api, O o, Looper looper) {
        this.vA = new AtomicBoolean(false);
        this.vB = new AtomicInteger(0);
        zzac.zzb((Object) context, (Object) "Null context is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.tv = api;
        this.vw = o;
        this.zzajn = looper;
        this.vv = new zzre();
        this.vx = zzpz.zza(this.tv, this.vw);
        this.vz = new zzqu(this);
        Pair zza = zzqt.zza(this.mContext, this);
        this.vy = (zzqt) zza.first;
        this.mId = ((Integer) zza.second).intValue();
    }

    private <A extends zzb, T extends zza<? extends Result, A>> T zza(int i, @NonNull T t) {
        t.zzaqt();
        this.vy.zza(this, i, (zza) t);
        return t;
    }

    private <TResult, A extends zzb> Task<TResult> zza(int i, @NonNull zzro<A, TResult> com_google_android_gms_internal_zzro_A__TResult) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.vy.zza(this, i, com_google_android_gms_internal_zzro_A__TResult, taskCompletionSource);
        return taskCompletionSource.getTask();
    }

    public int getInstanceId() {
        return this.mId;
    }

    public Looper getLooper() {
        return this.zzajn;
    }

    public void release() {
        boolean z = true;
        if (!this.vA.getAndSet(true)) {
            this.vv.release();
            zzqt com_google_android_gms_internal_zzqt = this.vy;
            int i = this.mId;
            if (this.vB.get() <= 0) {
                z = false;
            }
            com_google_android_gms_internal_zzqt.zzd(i, z);
        }
    }

    @WorkerThread
    public zze zza(Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        if (!zzapw()) {
            if (this.tv.zzapq()) {
                zzh zzapo = this.tv.zzapo();
                this.vC = new zzai(this.mContext, looper, zzapo.zzapt(), connectionCallbacks, onConnectionFailedListener, com.google.android.gms.common.internal.zzh.zzcd(this.mContext), zzapo.zzr(this.vw));
            } else {
                this.vC = this.tv.zzapn().zza(this.mContext, looper, com.google.android.gms.common.internal.zzh.zzcd(this.mContext), this.vw, connectionCallbacks, onConnectionFailedListener);
            }
        }
        return this.vC;
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zza(@NonNull T t) {
        return zza(0, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> zza(zzro<A, TResult> com_google_android_gms_internal_zzro_A__TResult) {
        return zza(0, (zzro) com_google_android_gms_internal_zzro_A__TResult);
    }

    public void zzapu() {
        this.vB.incrementAndGet();
    }

    public void zzapv() {
        if (this.vB.decrementAndGet() == 0 && this.vA.get()) {
            this.vy.zzd(this.mId, false);
        }
    }

    public boolean zzapw() {
        return this.vC != null;
    }

    public zzpz<O> zzapx() {
        return this.vx;
    }

    public GoogleApiClient zzapy() {
        return this.vz;
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(@NonNull T t) {
        return zza(1, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> zzb(zzro<A, TResult> com_google_android_gms_internal_zzro_A__TResult) {
        return zza(1, (zzro) com_google_android_gms_internal_zzro_A__TResult);
    }
}
