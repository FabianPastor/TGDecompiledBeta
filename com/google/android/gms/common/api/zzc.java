package com.google.android.gms.common.api;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzaae;
import com.google.android.gms.internal.zzaap;
import com.google.android.gms.internal.zzaaq;
import com.google.android.gms.internal.zzaaz;
import com.google.android.gms.internal.zzaba;
import com.google.android.gms.internal.zzabe;
import com.google.android.gms.internal.zzabj;
import com.google.android.gms.internal.zzabk;
import com.google.android.gms.internal.zzabn;
import com.google.android.gms.internal.zzabr;
import com.google.android.gms.internal.zzzr;
import com.google.android.gms.internal.zzzs;
import com.google.android.gms.internal.zzzv.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzc<O extends ApiOptions> {
    private final Context mContext;
    private final int mId;
    private final Api<O> zzawb;
    private final O zzaxG;
    private final zzzs<O> zzaxH;
    private final GoogleApiClient zzaxI;
    private final zzabk zzaxJ;
    protected final zzaap zzaxK;
    private final Looper zzrx;

    @MainThread
    public zzc(@NonNull Activity activity, Api<O> api, O o, Looper looper, zzabk com_google_android_gms_internal_zzabk) {
        zzac.zzb((Object) activity, (Object) "Null activity is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = activity.getApplicationContext();
        this.zzawb = api;
        this.zzaxG = o;
        this.zzrx = looper;
        this.zzaxH = zzzs.zza(this.zzawb, this.zzaxG);
        this.zzaxI = new zzaaq(this);
        this.zzaxK = zzaap.zzax(this.mContext);
        this.mId = this.zzaxK.zzvU();
        this.zzaxJ = com_google_android_gms_internal_zzabk;
        zzaae.zza(activity, this.zzaxK, this.zzaxH);
        this.zzaxK.zza(this);
    }

    public zzc(@NonNull Activity activity, Api<O> api, O o, zzabk com_google_android_gms_internal_zzabk) {
        this(activity, (Api) api, (ApiOptions) o, activity.getMainLooper(), com_google_android_gms_internal_zzabk);
    }

    protected zzc(@NonNull Context context, Api<O> api, Looper looper) {
        zzac.zzb((Object) context, (Object) "Null context is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.zzawb = api;
        this.zzaxG = null;
        this.zzrx = looper;
        this.zzaxH = zzzs.zzb(api);
        this.zzaxI = new zzaaq(this);
        this.zzaxK = zzaap.zzax(this.mContext);
        this.mId = this.zzaxK.zzvU();
        this.zzaxJ = new zzzr();
    }

    public zzc(@NonNull Context context, Api<O> api, O o, Looper looper, zzabk com_google_android_gms_internal_zzabk) {
        zzac.zzb((Object) context, (Object) "Null context is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.zzawb = api;
        this.zzaxG = o;
        this.zzrx = looper;
        this.zzaxH = zzzs.zza(this.zzawb, this.zzaxG);
        this.zzaxI = new zzaaq(this);
        this.zzaxK = zzaap.zzax(this.mContext);
        this.mId = this.zzaxK.zzvU();
        this.zzaxJ = com_google_android_gms_internal_zzabk;
        this.zzaxK.zza(this);
    }

    public zzc(@NonNull Context context, Api<O> api, O o, zzabk com_google_android_gms_internal_zzabk) {
        this(context, (Api) api, (ApiOptions) o, Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper(), com_google_android_gms_internal_zzabk);
    }

    private <A extends zzb, T extends zza<? extends Result, A>> T zza(int i, @NonNull T t) {
        t.zzvf();
        this.zzaxK.zza(this, i, (zza) t);
        return t;
    }

    private <TResult, A extends zzb> Task<TResult> zza(int i, @NonNull zzabn<A, TResult> com_google_android_gms_internal_zzabn_A__TResult) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.zzaxK.zza(this, i, com_google_android_gms_internal_zzabn_A__TResult, taskCompletionSource, this.zzaxJ);
        return taskCompletionSource.getTask();
    }

    public GoogleApiClient asGoogleApiClient() {
        return this.zzaxI;
    }

    @WorkerThread
    public zze buildApiClient(Looper looper, zzaap.zza<O> com_google_android_gms_internal_zzaap_zza_O) {
        return this.zzawb.zzuG().zza(this.mContext, looper, zzg.zzaA(this.mContext), this.zzaxG, com_google_android_gms_internal_zzaap_zza_O, com_google_android_gms_internal_zzaap_zza_O);
    }

    public zzabj createSignInCoordinator(Context context, Handler handler) {
        return new zzabj(context, handler);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T doBestEffortWrite(@NonNull T t) {
        return zza(2, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doBestEffortWrite(zzabn<A, TResult> com_google_android_gms_internal_zzabn_A__TResult) {
        return zza(2, (zzabn) com_google_android_gms_internal_zzabn_A__TResult);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T doRead(@NonNull T t) {
        return zza(0, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doRead(zzabn<A, TResult> com_google_android_gms_internal_zzabn_A__TResult) {
        return zza(0, (zzabn) com_google_android_gms_internal_zzabn_A__TResult);
    }

    public <A extends zzb, T extends zzabe<A, ?>, U extends zzabr<A, ?>> Task<Void> doRegisterEventListener(@NonNull T t, U u) {
        zzac.zzw(t);
        zzac.zzw(u);
        zzac.zzb(t.zzwp(), (Object) "Listener has already been released.");
        zzac.zzb(u.zzwp(), (Object) "Listener has already been released.");
        zzac.zzb(t.zzwp().equals(u.zzwp()), (Object) "Listener registration and unregistration methods must be constructed with the same ListenerHolder.");
        return this.zzaxK.zza(this, (zzabe) t, (zzabr) u);
    }

    public Task<Void> doUnregisterEventListener(@NonNull zzaaz.zzb<?> com_google_android_gms_internal_zzaaz_zzb_) {
        zzac.zzb((Object) com_google_android_gms_internal_zzaaz_zzb_, (Object) "Listener key cannot be null.");
        return this.zzaxK.zza(this, (zzaaz.zzb) com_google_android_gms_internal_zzaaz_zzb_);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T doWrite(@NonNull T t) {
        return zza(1, (zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doWrite(zzabn<A, TResult> com_google_android_gms_internal_zzabn_A__TResult) {
        return zza(1, (zzabn) com_google_android_gms_internal_zzabn_A__TResult);
    }

    public Api<O> getApi() {
        return this.zzawb;
    }

    public zzzs<O> getApiKey() {
        return this.zzaxH;
    }

    public O getApiOptions() {
        return this.zzaxG;
    }

    public Context getApplicationContext() {
        return this.mContext;
    }

    public int getInstanceId() {
        return this.mId;
    }

    public Looper getLooper() {
        return this.zzrx;
    }

    public <L> zzaaz<L> registerListener(@NonNull L l, String str) {
        return zzaba.zzb(l, this.zzrx, str);
    }
}
