package com.google.android.gms.common.api;

import android.accounts.Account;
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
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzaam;
import com.google.android.gms.internal.zzaax;
import com.google.android.gms.internal.zzaay;
import com.google.android.gms.internal.zzabh;
import com.google.android.gms.internal.zzabi;
import com.google.android.gms.internal.zzabm;
import com.google.android.gms.internal.zzabr;
import com.google.android.gms.internal.zzabs;
import com.google.android.gms.internal.zzabv;
import com.google.android.gms.internal.zzabz;
import com.google.android.gms.internal.zzzy;
import com.google.android.gms.internal.zzzz;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzc<O extends ApiOptions> {
    private final Context mContext;
    private final int mId;
    private final Account zzahh;
    private final Api<O> zzaxf;
    private final O zzayT;
    private final zzzz<O> zzayU;
    private final GoogleApiClient zzayV;
    private final zzabs zzayW;
    protected final zzaax zzayX;
    private final Looper zzrs;

    public static class zza {
        public static final zza zzayY = new zza().zzvk();
        public final Account account;
        public final zzabs zzayZ;
        public final Looper zzaza;

        public static class zza {
            private zzabs zzayW;
            private Looper zzrs;

            public zza zza(zzabs com_google_android_gms_internal_zzabs) {
                zzac.zzb((Object) com_google_android_gms_internal_zzabs, (Object) "StatusExceptionMapper must not be null.");
                this.zzayW = com_google_android_gms_internal_zzabs;
                return this;
            }

            public zza zzb(Looper looper) {
                zzac.zzb((Object) looper, (Object) "Looper must not be null.");
                this.zzrs = looper;
                return this;
            }

            public zza zzvk() {
                if (this.zzayW == null) {
                    this.zzayW = new zzzy();
                }
                if (this.zzrs == null) {
                    if (Looper.myLooper() != null) {
                        this.zzrs = Looper.myLooper();
                    } else {
                        this.zzrs = Looper.getMainLooper();
                    }
                }
                return new zza(this.zzayW, null, this.zzrs);
            }
        }

        private zza(zzabs com_google_android_gms_internal_zzabs, Account account, Looper looper) {
            this.zzayZ = com_google_android_gms_internal_zzabs;
            this.account = account;
            this.zzaza = looper;
        }
    }

    @Deprecated
    @MainThread
    public zzc(@NonNull Activity activity, Api<O> api, O o, Looper looper, zzabs com_google_android_gms_internal_zzabs) {
        this(activity, (Api) api, (ApiOptions) o, new zza().zzb(looper).zza(com_google_android_gms_internal_zzabs).zzvk());
    }

    @MainThread
    public zzc(@NonNull Activity activity, Api<O> api, O o, zza com_google_android_gms_common_api_zzc_zza) {
        zzac.zzb((Object) activity, (Object) "Null activity is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) com_google_android_gms_common_api_zzc_zza, (Object) "Settings must not be null; use Settings.createDefault() instead.");
        this.mContext = activity.getApplicationContext();
        this.zzaxf = api;
        this.zzayT = o;
        this.zzrs = com_google_android_gms_common_api_zzc_zza.zzaza;
        this.zzayU = zzzz.zza(this.zzaxf, this.zzayT);
        this.zzayV = new zzaay(this);
        this.zzayX = zzaax.zzaP(this.mContext);
        this.mId = this.zzayX.zzwz();
        this.zzayW = com_google_android_gms_common_api_zzc_zza.zzayZ;
        this.zzahh = com_google_android_gms_common_api_zzc_zza.account;
        zzaam.zza(activity, this.zzayX, this.zzayU);
        this.zzayX.zzb(this);
    }

    @Deprecated
    public zzc(@NonNull Activity activity, Api<O> api, O o, zzabs com_google_android_gms_internal_zzabs) {
        this(activity, (Api) api, (ApiOptions) o, new zza().zza(com_google_android_gms_internal_zzabs).zzb(activity.getMainLooper()).zzvk());
    }

    protected zzc(@NonNull Context context, Api<O> api, Looper looper) {
        zzac.zzb((Object) context, (Object) "Null context is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.zzaxf = api;
        this.zzayT = null;
        this.zzrs = looper;
        this.zzayU = zzzz.zzb(api);
        this.zzayV = new zzaay(this);
        this.zzayX = zzaax.zzaP(this.mContext);
        this.mId = this.zzayX.zzwz();
        this.zzayW = new zzzy();
        this.zzahh = null;
    }

    @Deprecated
    public zzc(@NonNull Context context, Api<O> api, O o, Looper looper, zzabs com_google_android_gms_internal_zzabs) {
        this(context, (Api) api, (ApiOptions) o, new zza().zzb(looper).zza(com_google_android_gms_internal_zzabs).zzvk());
    }

    public zzc(@NonNull Context context, Api<O> api, O o, zza com_google_android_gms_common_api_zzc_zza) {
        zzac.zzb((Object) context, (Object) "Null context is not permitted.");
        zzac.zzb((Object) api, (Object) "Api must not be null.");
        zzac.zzb((Object) com_google_android_gms_common_api_zzc_zza, (Object) "Settings must not be null; use Settings.createDefault() instead.");
        this.mContext = context.getApplicationContext();
        this.zzaxf = api;
        this.zzayT = o;
        this.zzrs = com_google_android_gms_common_api_zzc_zza.zzaza;
        this.zzayU = zzzz.zza(this.zzaxf, this.zzayT);
        this.zzayV = new zzaay(this);
        this.zzayX = zzaax.zzaP(this.mContext);
        this.mId = this.zzayX.zzwz();
        this.zzayW = com_google_android_gms_common_api_zzc_zza.zzayZ;
        this.zzahh = com_google_android_gms_common_api_zzc_zza.account;
        this.zzayX.zzb(this);
    }

    @Deprecated
    public zzc(@NonNull Context context, Api<O> api, O o, zzabs com_google_android_gms_internal_zzabs) {
        this(context, (Api) api, (ApiOptions) o, new zza().zza(com_google_android_gms_internal_zzabs).zzvk());
    }

    private <A extends zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T zza(int i, @NonNull T t) {
        t.zzvI();
        this.zzayX.zza(this, i, (com.google.android.gms.internal.zzaad.zza) t);
        return t;
    }

    private <TResult, A extends zzb> Task<TResult> zza(int i, @NonNull zzabv<A, TResult> com_google_android_gms_internal_zzabv_A__TResult) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.zzayX.zza(this, i, com_google_android_gms_internal_zzabv_A__TResult, taskCompletionSource, this.zzayW);
        return taskCompletionSource.getTask();
    }

    public GoogleApiClient asGoogleApiClient() {
        return this.zzayV;
    }

    @WorkerThread
    public zze buildApiClient(Looper looper, com.google.android.gms.internal.zzaax.zza<O> com_google_android_gms_internal_zzaax_zza_O) {
        return this.zzaxf.zzvf().zza(this.mContext, looper, new Builder(this.mContext).zze(this.zzahh).zzvp(), this.zzayT, com_google_android_gms_internal_zzaax_zza_O, com_google_android_gms_internal_zzaax_zza_O);
    }

    public zzabr createSignInCoordinator(Context context, Handler handler) {
        return new zzabr(context, handler);
    }

    public <A extends zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T doBestEffortWrite(@NonNull T t) {
        return zza(2, (com.google.android.gms.internal.zzaad.zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doBestEffortWrite(zzabv<A, TResult> com_google_android_gms_internal_zzabv_A__TResult) {
        return zza(2, (zzabv) com_google_android_gms_internal_zzabv_A__TResult);
    }

    public <A extends zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T doRead(@NonNull T t) {
        return zza(0, (com.google.android.gms.internal.zzaad.zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doRead(zzabv<A, TResult> com_google_android_gms_internal_zzabv_A__TResult) {
        return zza(0, (zzabv) com_google_android_gms_internal_zzabv_A__TResult);
    }

    public <A extends zzb, T extends zzabm<A, ?>, U extends zzabz<A, ?>> Task<Void> doRegisterEventListener(@NonNull T t, U u) {
        zzac.zzw(t);
        zzac.zzw(u);
        zzac.zzb(t.zzwW(), (Object) "Listener has already been released.");
        zzac.zzb(u.zzwW(), (Object) "Listener has already been released.");
        zzac.zzb(t.zzwW().equals(u.zzwW()), (Object) "Listener registration and unregistration methods must be constructed with the same ListenerHolder.");
        return this.zzayX.zza(this, (zzabm) t, (zzabz) u);
    }

    public Task<Void> doUnregisterEventListener(@NonNull zzabh.zzb<?> com_google_android_gms_internal_zzabh_zzb_) {
        zzac.zzb((Object) com_google_android_gms_internal_zzabh_zzb_, (Object) "Listener key cannot be null.");
        return this.zzayX.zza(this, (zzabh.zzb) com_google_android_gms_internal_zzabh_zzb_);
    }

    public <A extends zzb, T extends com.google.android.gms.internal.zzaad.zza<? extends Result, A>> T doWrite(@NonNull T t) {
        return zza(1, (com.google.android.gms.internal.zzaad.zza) t);
    }

    public <TResult, A extends zzb> Task<TResult> doWrite(zzabv<A, TResult> com_google_android_gms_internal_zzabv_A__TResult) {
        return zza(1, (zzabv) com_google_android_gms_internal_zzabv_A__TResult);
    }

    public Api<O> getApi() {
        return this.zzaxf;
    }

    public zzzz<O> getApiKey() {
        return this.zzayU;
    }

    public O getApiOptions() {
        return this.zzayT;
    }

    public Context getApplicationContext() {
        return this.mContext;
    }

    public int getInstanceId() {
        return this.mId;
    }

    public Looper getLooper() {
        return this.zzrs;
    }

    public <L> zzabh<L> registerListener(@NonNull L l, String str) {
        return zzabi.zzb(l, this.zzrs, str);
    }
}
