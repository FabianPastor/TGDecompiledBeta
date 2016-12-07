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
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzad;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzaxv.zza;

public class zzaxy extends zzl<zzaxv> implements zzaxn {
    private Integer zzaEe;
    private final zzg zzazs;
    private final Bundle zzbCf;
    private final boolean zzbCq;

    public zzaxy(Context context, Looper looper, boolean z, zzg com_google_android_gms_common_internal_zzg, Bundle bundle, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 44, com_google_android_gms_common_internal_zzg, connectionCallbacks, onConnectionFailedListener);
        this.zzbCq = z;
        this.zzazs = com_google_android_gms_common_internal_zzg;
        this.zzbCf = bundle;
        this.zzaEe = com_google_android_gms_common_internal_zzg.zzxl();
    }

    public zzaxy(Context context, Looper looper, boolean z, zzg com_google_android_gms_common_internal_zzg, zzaxo com_google_android_gms_internal_zzaxo, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, z, com_google_android_gms_common_internal_zzg, zza(com_google_android_gms_common_internal_zzg), connectionCallbacks, onConnectionFailedListener);
    }

    private zzad zzOn() {
        Account zzwU = this.zzazs.zzwU();
        GoogleSignInAccount googleSignInAccount = null;
        if ("<<default account>>".equals(zzwU.name)) {
            googleSignInAccount = com.google.android.gms.auth.api.signin.internal.zzl.zzaa(getContext()).zzrc();
        }
        return new zzad(zzwU, this.zzaEe.intValue(), googleSignInAccount);
    }

    public static Bundle zza(zzg com_google_android_gms_common_internal_zzg) {
        zzaxo zzxk = com_google_android_gms_common_internal_zzg.zzxk();
        Integer zzxl = com_google_android_gms_common_internal_zzg.zzxl();
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", com_google_android_gms_common_internal_zzg.getAccount());
        if (zzxl != null) {
            bundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", zzxl.intValue());
        }
        if (zzxk != null) {
            bundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", zzxk.zzOf());
            bundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", zzxk.zzqK());
            bundle.putString("com.google.android.gms.signin.internal.serverClientId", zzxk.zzqN());
            bundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
            bundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", zzxk.zzqM());
            bundle.putString("com.google.android.gms.signin.internal.hostedDomain", zzxk.zzqO());
            bundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", zzxk.zzOg());
            if (zzxk.zzOh() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", zzxk.zzOh().longValue());
            }
            if (zzxk.zzOi() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", zzxk.zzOi().longValue());
            }
        }
        return bundle;
    }

    public void connect() {
        zza(new zzi(this));
    }

    public void zzOe() {
        try {
            ((zzaxv) zzwW()).zzmK(this.zzaEe.intValue());
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
        }
    }

    public void zza(zzr com_google_android_gms_common_internal_zzr, boolean z) {
        try {
            ((zzaxv) zzwW()).zza(com_google_android_gms_common_internal_zzr, this.zzaEe.intValue(), z);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
        }
    }

    public void zza(zzaxu com_google_android_gms_internal_zzaxu) {
        zzac.zzb((Object) com_google_android_gms_internal_zzaxu, (Object) "Expecting a valid ISignInCallbacks");
        try {
            ((zzaxv) zzwW()).zza(new zzaxz(zzOn()), com_google_android_gms_internal_zzaxu);
        } catch (Throwable e) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                com_google_android_gms_internal_zzaxu.zzb(new zzayb(8));
            } catch (RemoteException e2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e);
            }
        }
    }

    protected zzaxv zzeZ(IBinder iBinder) {
        return zza.zzeY(iBinder);
    }

    protected String zzeu() {
        return "com.google.android.gms.signin.service.START";
    }

    protected String zzev() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }

    protected /* synthetic */ IInterface zzh(IBinder iBinder) {
        return zzeZ(iBinder);
    }

    public boolean zzqD() {
        return this.zzbCq;
    }

    protected Bundle zzql() {
        if (!getContext().getPackageName().equals(this.zzazs.zzxh())) {
            this.zzbCf.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzazs.zzxh());
        }
        return this.zzbCf;
    }
}
