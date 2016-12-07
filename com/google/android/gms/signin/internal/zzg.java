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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzwz;
import com.google.android.gms.internal.zzxa;
import com.google.android.gms.signin.internal.zze.zza;

public class zzg extends zzl<zze> implements zzwz {
    private Integer Ca;
    private final boolean aAk;
    private final Bundle aAl;
    private final zzh xB;

    public zzg(Context context, Looper looper, boolean z, zzh com_google_android_gms_common_internal_zzh, Bundle bundle, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 44, com_google_android_gms_common_internal_zzh, connectionCallbacks, onConnectionFailedListener);
        this.aAk = z;
        this.xB = com_google_android_gms_common_internal_zzh;
        this.aAl = bundle;
        this.Ca = com_google_android_gms_common_internal_zzh.zzaun();
    }

    public zzg(Context context, Looper looper, boolean z, zzh com_google_android_gms_common_internal_zzh, zzxa com_google_android_gms_internal_zzxa, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, z, com_google_android_gms_common_internal_zzh, zza(com_google_android_gms_common_internal_zzh), connectionCallbacks, onConnectionFailedListener);
    }

    public static Bundle zza(zzh com_google_android_gms_common_internal_zzh) {
        zzxa zzaum = com_google_android_gms_common_internal_zzh.zzaum();
        Integer zzaun = com_google_android_gms_common_internal_zzh.zzaun();
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", com_google_android_gms_common_internal_zzh.getAccount());
        if (zzaun != null) {
            bundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", zzaun.intValue());
        }
        if (zzaum != null) {
            bundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", zzaum.zzcdb());
            bundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", zzaum.zzahk());
            bundle.putString("com.google.android.gms.signin.internal.serverClientId", zzaum.zzahn());
            bundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
            bundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", zzaum.zzahm());
            bundle.putString("com.google.android.gms.signin.internal.hostedDomain", zzaum.zzaho());
            bundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", zzaum.zzcdc());
            if (zzaum.zzcdd() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", zzaum.zzcdd().longValue());
            }
            if (zzaum.zzcde() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", zzaum.zzcde().longValue());
            }
        }
        return bundle;
    }

    private ResolveAccountRequest zzcdj() {
        Account zzatv = this.xB.zzatv();
        GoogleSignInAccount googleSignInAccount = null;
        if ("<<default account>>".equals(zzatv.name)) {
            googleSignInAccount = zzk.zzbd(getContext()).zzaic();
        }
        return new ResolveAccountRequest(zzatv, this.Ca.intValue(), googleSignInAccount);
    }

    public void connect() {
        zza(new zzi(this));
    }

    public void zza(zzr com_google_android_gms_common_internal_zzr, boolean z) {
        try {
            ((zze) zzatx()).zza(com_google_android_gms_common_internal_zzr, this.Ca.intValue(), z);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
        }
    }

    public void zza(zzd com_google_android_gms_signin_internal_zzd) {
        zzac.zzb((Object) com_google_android_gms_signin_internal_zzd, (Object) "Expecting a valid ISignInCallbacks");
        try {
            ((zze) zzatx()).zza(new SignInRequest(zzcdj()), com_google_android_gms_signin_internal_zzd);
        } catch (Throwable e) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                com_google_android_gms_signin_internal_zzd.zzb(new SignInResponse(8));
            } catch (RemoteException e2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e);
            }
        }
    }

    protected Bundle zzagl() {
        if (!getContext().getPackageName().equals(this.xB.zzauj())) {
            this.aAl.putString("com.google.android.gms.signin.internal.realClientPackageName", this.xB.zzauj());
        }
        return this.aAl;
    }

    public boolean zzahd() {
        return this.aAk;
    }

    public void zzcda() {
        try {
            ((zze) zzatx()).zzaaf(this.Ca.intValue());
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
        }
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzlc(iBinder);
    }

    protected String zzix() {
        return "com.google.android.gms.signin.service.START";
    }

    protected String zziy() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }

    protected zze zzlc(IBinder iBinder) {
        return zza.zzlb(iBinder);
    }
}
