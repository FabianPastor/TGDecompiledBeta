package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.ApiOptions.HasAccountOptions;
import com.google.android.gms.common.api.Api.ApiOptions.HasGoogleSignInAccountOptions;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.internal.zzbm;
import com.google.android.gms.common.api.internal.zzbo;
import com.google.android.gms.common.api.internal.zzbw;
import com.google.android.gms.common.api.internal.zzcv;
import com.google.android.gms.common.api.internal.zzcz;
import com.google.android.gms.common.api.internal.zzg;
import com.google.android.gms.common.api.internal.zzh;
import com.google.android.gms.common.api.internal.zzm;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzs;
import java.util.Collection;
import java.util.Collections;

public class GoogleApi<O extends ApiOptions> {
    private final Context mContext;
    private final int mId;
    private final Looper zzall;
    private final Api<O> zzfin;
    private final O zzfme = null;
    private final zzh<O> zzfmf;
    private final GoogleApiClient zzfmg;
    private final zzcz zzfmh;
    protected final zzbm zzfmi;

    protected GoogleApi(Context context, Api<O> api, Looper looper) {
        zzbq.checkNotNull(context, "Null context is not permitted.");
        zzbq.checkNotNull(api, "Api must not be null.");
        zzbq.checkNotNull(looper, "Looper must not be null.");
        this.mContext = context.getApplicationContext();
        this.zzfin = api;
        this.zzall = looper;
        this.zzfmf = zzh.zzb(api);
        this.zzfmg = new zzbw(this);
        this.zzfmi = zzbm.zzcj(this.mContext);
        this.mId = this.zzfmi.zzais();
        this.zzfmh = new zzg();
    }

    private final <A extends zzb, T extends zzm<? extends Result, A>> T zza(int i, T t) {
        t.zzahi();
        this.zzfmi.zza(this, i, t);
        return t;
    }

    private final zzs zzagp() {
        GoogleSignInAccount googleSignInAccount;
        Account account;
        Collection zzabb;
        zzs com_google_android_gms_common_internal_zzs = new zzs();
        if (this.zzfme instanceof HasGoogleSignInAccountOptions) {
            googleSignInAccount = ((HasGoogleSignInAccountOptions) this.zzfme).getGoogleSignInAccount();
            if (googleSignInAccount != null) {
                account = googleSignInAccount.getAccount();
                com_google_android_gms_common_internal_zzs = com_google_android_gms_common_internal_zzs.zze(account);
                if (this.zzfme instanceof HasGoogleSignInAccountOptions) {
                    googleSignInAccount = ((HasGoogleSignInAccountOptions) this.zzfme).getGoogleSignInAccount();
                    if (googleSignInAccount != null) {
                        zzabb = googleSignInAccount.zzabb();
                        return com_google_android_gms_common_internal_zzs.zze(zzabb);
                    }
                }
                zzabb = Collections.emptySet();
                return com_google_android_gms_common_internal_zzs.zze(zzabb);
            }
        }
        account = this.zzfme instanceof HasAccountOptions ? ((HasAccountOptions) this.zzfme).getAccount() : null;
        com_google_android_gms_common_internal_zzs = com_google_android_gms_common_internal_zzs.zze(account);
        if (this.zzfme instanceof HasGoogleSignInAccountOptions) {
            googleSignInAccount = ((HasGoogleSignInAccountOptions) this.zzfme).getGoogleSignInAccount();
            if (googleSignInAccount != null) {
                zzabb = googleSignInAccount.zzabb();
                return com_google_android_gms_common_internal_zzs.zze(zzabb);
            }
        }
        zzabb = Collections.emptySet();
        return com_google_android_gms_common_internal_zzs.zze(zzabb);
    }

    public final int getInstanceId() {
        return this.mId;
    }

    public final Looper getLooper() {
        return this.zzall;
    }

    public zze zza(Looper looper, zzbo<O> com_google_android_gms_common_api_internal_zzbo_O) {
        return this.zzfin.zzage().zza(this.mContext, looper, zzagp().zzgf(this.mContext.getPackageName()).zzgg(this.mContext.getClass().getName()).zzald(), this.zzfme, com_google_android_gms_common_api_internal_zzbo_O, com_google_android_gms_common_api_internal_zzbo_O);
    }

    public zzcv zza(Context context, Handler handler) {
        return new zzcv(context, handler, zzagp().zzald());
    }

    public final <A extends zzb, T extends zzm<? extends Result, A>> T zza(T t) {
        return zza(0, (zzm) t);
    }

    public final Api<O> zzagl() {
        return this.zzfin;
    }

    public final zzh<O> zzagn() {
        return this.zzfmf;
    }

    public final <A extends zzb, T extends zzm<? extends Result, A>> T zzb(T t) {
        return zza(1, (zzm) t);
    }
}
