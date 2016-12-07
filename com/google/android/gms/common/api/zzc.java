package com.google.android.gms.common.api;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Api.zzh;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzag;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.internal.zzqk;
import com.google.android.gms.internal.zzql;
import com.google.android.gms.internal.zzqo.zza;
import com.google.android.gms.internal.zzqr;
import com.google.android.gms.internal.zzqw;
import com.google.android.gms.internal.zzrh;
import com.google.android.gms.internal.zzri;
import com.google.android.gms.internal.zzrr;
import com.google.android.gms.internal.zzrs;
import com.google.android.gms.internal.zzrw;
import com.google.android.gms.internal.zzsb;
import com.google.android.gms.internal.zzse;
import com.google.android.gms.internal.zzsh;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzc<O extends ApiOptions> {
    private final Context mContext;
    private final int mId;
    private final Api<O> vS;
    private final zzsb xA;
    private final zze xB;
    private final zzqr xC;
    private final O xw;
    private final zzql<O> xx;
    private final zzrh xy;
    private final GoogleApiClient xz;
    private final Looper zzajy;

    @MainThread
    public zzc(@NonNull Activity activity, Api<O> api, O o, Looper looper, zzsb com_google_android_gms_internal_zzsb) {
        zzaa.zzb((Object) activity, (Object) "Null activity is not permitted.");
        zzaa.zzb((Object) api, (Object) "Api must not be null.");
        zzaa.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = activity.getApplicationContext();
        this.vS = api;
        this.xw = o;
        this.zzajy = looper;
        this.xx = zzql.zza(this.vS, this.xw);
        this.xz = new zzri(this);
        this.xy = zzrh.zzbx(this.mContext);
        this.mId = this.xy.zzath();
        this.xA = com_google_android_gms_internal_zzsb;
        this.xB = null;
        this.xC = null;
        zzqw.zza(activity, this.xy, this.xx);
        this.xy.zza(this);
    }

    public zzc(@NonNull Activity activity, Api<O> api, O o, zzsb com_google_android_gms_internal_zzsb) {
        this(activity, (Api) api, (ApiOptions) o, activity.getMainLooper(), com_google_android_gms_internal_zzsb);
    }

    protected zzc(@NonNull Context context, Api<O> api, Looper looper, zze com_google_android_gms_common_api_Api_zze, zzqr com_google_android_gms_internal_zzqr) {
        zzaa.zzb((Object) context, (Object) "Null context is not permitted.");
        zzaa.zzb((Object) api, (Object) "Api must not be null.");
        zzaa.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.vS = api;
        this.xw = null;
        this.zzajy = looper;
        this.xx = zzql.zzb(api);
        this.xz = new zzri(this);
        this.xy = zzrh.zzbx(this.mContext);
        this.mId = this.xy.zzath();
        this.xA = new zzqk();
        this.xB = com_google_android_gms_common_api_Api_zze;
        this.xC = com_google_android_gms_internal_zzqr;
        this.xy.zza(this);
    }

    public zzc(@NonNull Context context, Api<O> api, O o, Looper looper, zzsb com_google_android_gms_internal_zzsb) {
        zzaa.zzb((Object) context, (Object) "Null context is not permitted.");
        zzaa.zzb((Object) api, (Object) "Api must not be null.");
        zzaa.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.vS = api;
        this.xw = o;
        this.zzajy = looper;
        this.xx = zzql.zza(this.vS, this.xw);
        this.xz = new zzri(this);
        this.xy = zzrh.zzbx(this.mContext);
        this.mId = this.xy.zzath();
        this.xA = com_google_android_gms_internal_zzsb;
        this.xB = null;
        this.xC = null;
        this.xy.zza(this);
    }

    public zzc(@NonNull Context context, Api<O> api, O o, zzsb com_google_android_gms_internal_zzsb) {
        this(context, (Api) api, (ApiOptions) o, Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper(), com_google_android_gms_internal_zzsb);
    }

    private <A extends zzb, T extends zza<? extends Result, A>> T zza(int i, @NonNull T t) {
        t.zzarv();
        this.xy.zza(this, i, (zza) t);
        return t;
    }

    private <TResult, A extends zzb> Task<TResult> zza(int i, @NonNull zzse<A, TResult> com_google_android_gms_internal_zzse_A__TResult) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.xy.zza(this, i, com_google_android_gms_internal_zzse_A__TResult, taskCompletionSource, this.xA);
        return taskCompletionSource.getTask();
    }

    public GoogleApiClient asGoogleApiClient() {
        return this.xz;
    }

    @WorkerThread
    public zze buildApiClient(Looper looper, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        zzaa.zza(this.xB == null, (Object) "Client is already built, use getClient(). getClientCallbacks() should also be provided with a helper.");
        if (this.vS.zzaqw()) {
            zzh zzaqu = this.vS.zzaqu();
            return new zzag(this.mContext, looper, zzaqu.zzaqz(), connectionCallbacks, onConnectionFailedListener, zzf.zzca(this.mContext), zzaqu.zzr(this.xw));
        }
        return this.vS.zzaqt().zza(this.mContext, looper, zzf.zzca(this.mContext), this.xw, connectionCallbacks, onConnectionFailedListener);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T doBestEffortWrite(@NonNull T t) {
        return zza(2, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doBestEffortWrite(zzse<A, TResult> com_google_android_gms_internal_zzse_A__TResult) {
        return zza(2, (zzse) com_google_android_gms_internal_zzse_A__TResult);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T doRead(@NonNull T t) {
        return zza(0, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doRead(zzse<A, TResult> com_google_android_gms_internal_zzse_A__TResult) {
        return zza(0, (zzse) com_google_android_gms_internal_zzse_A__TResult);
    }

    public <A extends zzb, T extends zzrw<A>, U extends zzsh<A>> Task<Void> doRegisterEventListener(@NonNull T t, U u) {
        zzaa.zzy(t);
        zzaa.zzy(u);
        zzaa.zzb(t.zzatz(), (Object) "Listener has already been released.");
        zzaa.zzb(u.zzatz(), (Object) "Listener has already been released.");
        zzaa.zzb(t.zzatz().equals(u.zzatz()), (Object) "Listener registration and unregistration methods must be constructed with the same ListenerHolder.");
        return this.xy.zza(this, (zzrw) t, (zzsh) u);
    }

    public Task<Void> doUnregisterEventListener(@NonNull zzrr.zzb<?> com_google_android_gms_internal_zzrr_zzb_) {
        zzaa.zzb((Object) com_google_android_gms_internal_zzrr_zzb_, (Object) "Listener key cannot be null.");
        return this.xy.zza(this, (zzrr.zzb) com_google_android_gms_internal_zzrr_zzb_);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T doWrite(@NonNull T t) {
        return zza(1, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doWrite(zzse<A, TResult> com_google_android_gms_internal_zzse_A__TResult) {
        return zza(1, (zzse) com_google_android_gms_internal_zzse_A__TResult);
    }

    public Api<O> getApi() {
        return this.vS;
    }

    public zzql<O> getApiKey() {
        return this.xx;
    }

    public O getApiOptions() {
        return this.xw;
    }

    public Context getApplicationContext() {
        return this.mContext;
    }

    public zze getClient() {
        return (zze) zzaa.zzb(this.xB, (Object) "Client is null, buildApiClient() should be used.");
    }

    public zzqr getClientCallbacks() {
        return (zzqr) zzaa.zzb(this.xC, (Object) "ClientCallbacks is null.");
    }

    public int getInstanceId() {
        return this.mId;
    }

    public Looper getLooper() {
        return this.zzajy;
    }

    public boolean isConnectionlessGoogleApiClient() {
        return (this.xB == null || this.xC == null) ? false : true;
    }

    public <L> zzrr<L> registerListener(@NonNull L l, String str) {
        return zzrs.zzb(l, this.zzajy, str);
    }
}
