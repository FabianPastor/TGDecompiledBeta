package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.zzk;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzp;
import com.google.android.gms.internal.zzxp;
import com.google.android.gms.internal.zzxq;
import com.google.android.gms.signin.internal.zze.zza;

public class zzg extends zzj<zze> implements zzxp {
    private Integer DM;
    private final Bundle aDk;
    private final boolean aDv;
    private final zzf zP;

    public zzg(Context context, Looper looper, boolean z, zzf com_google_android_gms_common_internal_zzf, Bundle bundle, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 44, com_google_android_gms_common_internal_zzf, connectionCallbacks, onConnectionFailedListener);
        this.aDv = z;
        this.zP = com_google_android_gms_common_internal_zzf;
        this.aDk = bundle;
        this.DM = com_google_android_gms_common_internal_zzf.zzavw();
    }

    public zzg(Context context, Looper looper, boolean z, zzf com_google_android_gms_common_internal_zzf, zzxq com_google_android_gms_internal_zzxq, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, z, com_google_android_gms_common_internal_zzf, zza(com_google_android_gms_common_internal_zzf), connectionCallbacks, onConnectionFailedListener);
    }

    public static Bundle zza(zzf com_google_android_gms_common_internal_zzf) {
        zzxq zzavv = com_google_android_gms_common_internal_zzf.zzavv();
        Integer zzavw = com_google_android_gms_common_internal_zzf.zzavw();
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", com_google_android_gms_common_internal_zzf.getAccount());
        if (zzavw != null) {
            bundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", zzavw.intValue());
        }
        if (zzavv != null) {
            bundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", zzavv.zzcdd());
            bundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", zzavv.zzaiu());
            bundle.putString("com.google.android.gms.signin.internal.serverClientId", zzavv.zzaix());
            bundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
            bundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", zzavv.zzaiw());
            bundle.putString("com.google.android.gms.signin.internal.hostedDomain", zzavv.zzaiy());
            bundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", zzavv.zzcde());
            if (zzavv.zzcdf() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", zzavv.zzcdf().longValue());
            }
            if (zzavv.zzcdg() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", zzavv.zzcdg().longValue());
            }
        }
        return bundle;
    }

    private ResolveAccountRequest zzcdl() {
        Account zzave = this.zP.zzave();
        GoogleSignInAccount googleSignInAccount = null;
        if ("<<default account>>".equals(zzave.name)) {
            googleSignInAccount = zzk.zzba(getContext()).zzajm();
        }
        return new ResolveAccountRequest(zzave, this.DM.intValue(), googleSignInAccount);
    }

    public void connect() {
        zza(new zzi(this));
    }

    public void zza(zzp com_google_android_gms_common_internal_zzp, boolean z) {
        try {
            ((zze) zzavg()).zza(com_google_android_gms_common_internal_zzp, this.DM.intValue(), z);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
        }
    }

    public void zza(zzd com_google_android_gms_signin_internal_zzd) {
        zzaa.zzb((Object) com_google_android_gms_signin_internal_zzd, (Object) "Expecting a valid ISignInCallbacks");
        try {
            ((zze) zzavg()).zza(new SignInRequest(zzcdl()), com_google_android_gms_signin_internal_zzd);
        } catch (Throwable e) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                com_google_android_gms_signin_internal_zzd.zzb(new SignInResponse(8));
            } catch (RemoteException e2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e);
            }
        }
    }

    protected Bundle zzahv() {
        if (!getContext().getPackageName().equals(this.zP.zzavs())) {
            this.aDk.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zP.zzavs());
        }
        return this.aDk;
    }

    public boolean zzain() {
        return this.aDv;
    }

    public void zzcdc() {
        try {
            ((zze) zzavg()).zzzv(this.DM.intValue());
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
        }
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzkx(iBinder);
    }

    protected String zzjx() {
        return "com.google.android.gms.signin.service.START";
    }

    protected String zzjy() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }

    protected zze zzkx(IBinder iBinder) {
        return zza.zzkw(iBinder);
    }
}
