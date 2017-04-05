package com.google.android.gms.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.zzn;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzad;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzbaq.zza;

public class zzbat extends zzl<zzbaq> implements zzbai {
    private final zzg zzaAL;
    private Integer zzaFD;
    private final Bundle zzbEh;
    private final boolean zzbEs;

    public zzbat(Context context, Looper looper, boolean z, zzg com_google_android_gms_common_internal_zzg, Bundle bundle, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 44, com_google_android_gms_common_internal_zzg, connectionCallbacks, onConnectionFailedListener);
        this.zzbEs = z;
        this.zzaAL = com_google_android_gms_common_internal_zzg;
        this.zzbEh = bundle;
        this.zzaFD = com_google_android_gms_common_internal_zzg.zzxS();
    }

    public zzbat(Context context, Looper looper, boolean z, zzg com_google_android_gms_common_internal_zzg, zzbaj com_google_android_gms_internal_zzbaj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, z, com_google_android_gms_common_internal_zzg, zza(com_google_android_gms_common_internal_zzg), connectionCallbacks, onConnectionFailedListener);
    }

    private zzad zzPS() {
        Account zzxB = this.zzaAL.zzxB();
        GoogleSignInAccount googleSignInAccount = null;
        if ("<<default account>>".equals(zzxB.name)) {
            googleSignInAccount = zzn.zzas(getContext()).zzrB();
        }
        return new zzad(zzxB, this.zzaFD.intValue(), googleSignInAccount);
    }

    public static Bundle zza(zzg com_google_android_gms_common_internal_zzg) {
        zzbaj zzxR = com_google_android_gms_common_internal_zzg.zzxR();
        Integer zzxS = com_google_android_gms_common_internal_zzg.zzxS();
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", com_google_android_gms_common_internal_zzg.getAccount());
        if (zzxS != null) {
            bundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", zzxS.intValue());
        }
        if (zzxR != null) {
            bundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", zzxR.zzPK());
            bundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", zzxR.isIdTokenRequested());
            bundle.putString("com.google.android.gms.signin.internal.serverClientId", zzxR.getServerClientId());
            bundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
            bundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", zzxR.zzrl());
            bundle.putString("com.google.android.gms.signin.internal.hostedDomain", zzxR.zzrm());
            bundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", zzxR.zzPL());
            if (zzxR.zzPM() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", zzxR.zzPM().longValue());
            }
            if (zzxR.zzPN() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", zzxR.zzPN().longValue());
            }
        }
        return bundle;
    }

    public void connect() {
        zza(new zzi(this));
    }

    public void zzPJ() {
        try {
            ((zzbaq) zzxD()).zznv(this.zzaFD.intValue());
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
        }
    }

    public void zza(zzr com_google_android_gms_common_internal_zzr, boolean z) {
        try {
            ((zzbaq) zzxD()).zza(com_google_android_gms_common_internal_zzr, this.zzaFD.intValue(), z);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
        }
    }

    public void zza(zzbap com_google_android_gms_internal_zzbap) {
        zzac.zzb((Object) com_google_android_gms_internal_zzbap, (Object) "Expecting a valid ISignInCallbacks");
        try {
            ((zzbaq) zzxD()).zza(new zzbau(zzPS()), com_google_android_gms_internal_zzbap);
        } catch (Throwable e) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                com_google_android_gms_internal_zzbap.zzb(new zzbaw(8));
            } catch (RemoteException e2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e);
            }
        }
    }

    protected String zzeA() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }

    protected String zzez() {
        return "com.google.android.gms.signin.service.START";
    }

    protected zzbaq zzfg(IBinder iBinder) {
        return zza.zzff(iBinder);
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzfg(iBinder);
    }

    protected Bundle zzqL() {
        if (!getContext().getPackageName().equals(this.zzaAL.zzxO())) {
            this.zzbEh.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzaAL.zzxO());
        }
        return this.zzbEh;
    }

    public boolean zzrd() {
        return this.zzbEs;
    }
}
