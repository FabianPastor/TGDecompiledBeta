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
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbar;
import com.google.android.gms.internal.zzbas;
import com.google.android.gms.internal.zzbax;
import com.google.android.gms.internal.zzbbv;
import com.google.android.gms.internal.zzbda;
import com.google.android.gms.internal.zzbdc;
import com.google.android.gms.internal.zzbdi;
import com.google.android.gms.internal.zzbei;
import com.google.android.gms.internal.zzbel;
import com.google.android.gms.internal.zzbep;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class GoogleApi<O extends ApiOptions> {
    private final Context mContext;
    private final int mId;
    private final O zzaAJ;
    private final zzbas<O> zzaAK;
    private final GoogleApiClient zzaAL;
    private final zzbel zzaAM;
    protected final zzbda zzaAN;
    private final Account zzajb;
    private final Api<O> zzayW;
    private final Looper zzrO;

    public static class zza {
        public static final zza zzaAO = new zzd().zzpj();
        public final Account account;
        public final zzbel zzaAP;
        public final Looper zzaAQ;

        private zza(zzbel com_google_android_gms_internal_zzbel, Account account, Looper looper) {
            this.zzaAP = com_google_android_gms_internal_zzbel;
            this.account = account;
            this.zzaAQ = looper;
        }
    }

    @MainThread
    private GoogleApi(@NonNull Activity activity, Api<O> api, O o, zza com_google_android_gms_common_api_GoogleApi_zza) {
        zzbo.zzb((Object) activity, (Object) "Null activity is not permitted.");
        zzbo.zzb((Object) api, (Object) "Api must not be null.");
        zzbo.zzb((Object) com_google_android_gms_common_api_GoogleApi_zza, (Object) "Settings must not be null; use Settings.DEFAULT_SETTINGS instead.");
        this.mContext = activity.getApplicationContext();
        this.zzayW = api;
        this.zzaAJ = null;
        this.zzrO = com_google_android_gms_common_api_GoogleApi_zza.zzaAQ;
        this.zzaAK = zzbas.zza(this.zzayW, this.zzaAJ);
        this.zzaAL = new zzbdi(this);
        this.zzaAN = zzbda.zzay(this.mContext);
        this.mId = this.zzaAN.zzqm();
        this.zzaAM = com_google_android_gms_common_api_GoogleApi_zza.zzaAP;
        this.zzajb = com_google_android_gms_common_api_GoogleApi_zza.account;
        zzbbv.zza(activity, this.zzaAN, this.zzaAK);
        this.zzaAN.zzb(this);
    }

    @Deprecated
    public GoogleApi(@NonNull Activity activity, Api<O> api, O o, zzbel com_google_android_gms_internal_zzbel) {
        this(activity, (Api) api, null, new zzd().zza(com_google_android_gms_internal_zzbel).zza(activity.getMainLooper()).zzpj());
    }

    protected GoogleApi(@NonNull Context context, Api<O> api, Looper looper) {
        zzbo.zzb((Object) context, (Object) "Null context is not permitted.");
        zzbo.zzb((Object) api, (Object) "Api must not be null.");
        zzbo.zzb((Object) looper, (Object) "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.zzayW = api;
        this.zzaAJ = null;
        this.zzrO = looper;
        this.zzaAK = zzbas.zzb(api);
        this.zzaAL = new zzbdi(this);
        this.zzaAN = zzbda.zzay(this.mContext);
        this.mId = this.zzaAN.zzqm();
        this.zzaAM = new zzbar();
        this.zzajb = null;
    }

    @Deprecated
    public GoogleApi(@NonNull Context context, Api<O> api, O o, Looper looper, zzbel com_google_android_gms_internal_zzbel) {
        this(context, (Api) api, null, new zzd().zza(looper).zza(com_google_android_gms_internal_zzbel).zzpj());
    }

    public GoogleApi(@NonNull Context context, Api<O> api, O o, zza com_google_android_gms_common_api_GoogleApi_zza) {
        zzbo.zzb((Object) context, (Object) "Null context is not permitted.");
        zzbo.zzb((Object) api, (Object) "Api must not be null.");
        zzbo.zzb((Object) com_google_android_gms_common_api_GoogleApi_zza, (Object) "Settings must not be null; use Settings.DEFAULT_SETTINGS instead.");
        this.mContext = context.getApplicationContext();
        this.zzayW = api;
        this.zzaAJ = o;
        this.zzrO = com_google_android_gms_common_api_GoogleApi_zza.zzaAQ;
        this.zzaAK = zzbas.zza(this.zzayW, this.zzaAJ);
        this.zzaAL = new zzbdi(this);
        this.zzaAN = zzbda.zzay(this.mContext);
        this.mId = this.zzaAN.zzqm();
        this.zzaAM = com_google_android_gms_common_api_GoogleApi_zza.zzaAP;
        this.zzajb = com_google_android_gms_common_api_GoogleApi_zza.account;
        this.zzaAN.zzb(this);
    }

    @Deprecated
    public GoogleApi(@NonNull Context context, Api<O> api, O o, zzbel com_google_android_gms_internal_zzbel) {
        this(context, (Api) api, (ApiOptions) o, new zzd().zza(com_google_android_gms_internal_zzbel).zzpj());
    }

    private final <A extends zzb, T extends zzbax<? extends Result, A>> T zza(int i, @NonNull T t) {
        t.zzpC();
        this.zzaAN.zza(this, i, (zzbax) t);
        return t;
    }

    private final <TResult, A extends zzb> Task<TResult> zza(int i, @NonNull zzbep<A, TResult> com_google_android_gms_internal_zzbep_A__TResult) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.zzaAN.zza(this, i, com_google_android_gms_internal_zzbep_A__TResult, taskCompletionSource, this.zzaAM);
        return taskCompletionSource.getTask();
    }

    public final Context getApplicationContext() {
        return this.mContext;
    }

    public final int getInstanceId() {
        return this.mId;
    }

    public final Looper getLooper() {
        return this.zzrO;
    }

    @WorkerThread
    public zze zza(Looper looper, zzbdc<O> com_google_android_gms_internal_zzbdc_O) {
        return this.zzayW.zzpc().zza(this.mContext, looper, new Builder(this.mContext).zze(this.zzajb).zzpn(), this.zzaAJ, com_google_android_gms_internal_zzbdc_O, com_google_android_gms_internal_zzbdc_O);
    }

    public final <A extends zzb, T extends zzbax<? extends Result, A>> T zza(@NonNull T t) {
        return zza(0, (zzbax) t);
    }

    public zzbei zza(Context context, Handler handler) {
        return new zzbei(context, handler);
    }

    public final <TResult, A extends zzb> Task<TResult> zza(zzbep<A, TResult> com_google_android_gms_internal_zzbep_A__TResult) {
        return zza(0, (zzbep) com_google_android_gms_internal_zzbep_A__TResult);
    }

    public final <A extends zzb, T extends zzbax<? extends Result, A>> T zzb(@NonNull T t) {
        return zza(1, (zzbax) t);
    }

    public final <TResult, A extends zzb> Task<TResult> zzb(zzbep<A, TResult> com_google_android_gms_internal_zzbep_A__TResult) {
        return zza(1, (zzbep) com_google_android_gms_internal_zzbep_A__TResult);
    }

    public final <A extends zzb, T extends zzbax<? extends Result, A>> T zzc(@NonNull T t) {
        return zza(2, (zzbax) t);
    }

    public final Api<O> zzpg() {
        return this.zzayW;
    }

    public final zzbas<O> zzph() {
        return this.zzaAK;
    }

    public final GoogleApiClient zzpi() {
        return this.zzaAL;
    }
}
