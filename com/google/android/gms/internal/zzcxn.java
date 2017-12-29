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
import com.google.android.gms.auth.api.signin.internal.zzz;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzan;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzm;
import com.google.android.gms.common.internal.zzr;

public final class zzcxn extends zzab<zzcxl> implements zzcxd {
    private final zzr zzfpx;
    private Integer zzfzj;
    private final boolean zzkbz;
    private final Bundle zzkca;

    private zzcxn(Context context, Looper looper, boolean z, zzr com_google_android_gms_common_internal_zzr, Bundle bundle, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        super(context, looper, 44, com_google_android_gms_common_internal_zzr, connectionCallbacks, onConnectionFailedListener);
        this.zzkbz = true;
        this.zzfpx = com_google_android_gms_common_internal_zzr;
        this.zzkca = bundle;
        this.zzfzj = com_google_android_gms_common_internal_zzr.zzalc();
    }

    public zzcxn(Context context, Looper looper, boolean z, zzr com_google_android_gms_common_internal_zzr, zzcxe com_google_android_gms_internal_zzcxe, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        this(context, looper, true, com_google_android_gms_common_internal_zzr, zza(com_google_android_gms_common_internal_zzr), connectionCallbacks, onConnectionFailedListener);
    }

    public static Bundle zza(zzr com_google_android_gms_common_internal_zzr) {
        zzcxe zzalb = com_google_android_gms_common_internal_zzr.zzalb();
        Integer zzalc = com_google_android_gms_common_internal_zzr.zzalc();
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", com_google_android_gms_common_internal_zzr.getAccount());
        if (zzalc != null) {
            bundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", zzalc.intValue());
        }
        if (zzalb != null) {
            bundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", zzalb.zzbdc());
            bundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", zzalb.isIdTokenRequested());
            bundle.putString("com.google.android.gms.signin.internal.serverClientId", zzalb.getServerClientId());
            bundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
            bundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", zzalb.zzbdd());
            bundle.putString("com.google.android.gms.signin.internal.hostedDomain", zzalb.zzbde());
            bundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", zzalb.zzbdf());
            if (zzalb.zzbdg() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", zzalb.zzbdg().longValue());
            }
            if (zzalb.zzbdh() != null) {
                bundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", zzalb.zzbdh().longValue());
            }
        }
        return bundle;
    }

    public final void connect() {
        zza(new zzm(this));
    }

    public final void zza(zzan com_google_android_gms_common_internal_zzan, boolean z) {
        try {
            ((zzcxl) zzakn()).zza(com_google_android_gms_common_internal_zzan, this.zzfzj.intValue(), z);
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
        }
    }

    public final void zza(zzcxj com_google_android_gms_internal_zzcxj) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcxj, "Expecting a valid ISignInCallbacks");
        try {
            Account zzakt = this.zzfpx.zzakt();
            GoogleSignInAccount googleSignInAccount = null;
            if ("<<default account>>".equals(zzakt.name)) {
                googleSignInAccount = zzz.zzbt(getContext()).zzabt();
            }
            ((zzcxl) zzakn()).zza(new zzcxo(new zzbr(zzakt, this.zzfzj.intValue(), googleSignInAccount)), com_google_android_gms_internal_zzcxj);
        } catch (Throwable e) {
            Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
            try {
                com_google_android_gms_internal_zzcxj.zzb(new zzcxq(8));
            } catch (RemoteException e2) {
                Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", e);
            }
        }
    }

    protected final Bundle zzaap() {
        if (!getContext().getPackageName().equals(this.zzfpx.zzaky())) {
            this.zzkca.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzfpx.zzaky());
        }
        return this.zzkca;
    }

    public final boolean zzaay() {
        return this.zzkbz;
    }

    public final void zzbdb() {
        try {
            ((zzcxl) zzakn()).zzeh(this.zzfzj.intValue());
        } catch (RemoteException e) {
            Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
        }
    }

    protected final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.signin.internal.ISignInService");
        return queryLocalInterface instanceof zzcxl ? (zzcxl) queryLocalInterface : new zzcxm(iBinder);
    }

    protected final String zzhi() {
        return "com.google.android.gms.signin.service.START";
    }

    protected final String zzhj() {
        return "com.google.android.gms.signin.internal.ISignInService";
    }
}
