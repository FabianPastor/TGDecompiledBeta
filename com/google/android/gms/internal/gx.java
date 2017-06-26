package com.google.android.gms.internal;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import java.lang.ref.WeakReference;

final class gx extends gv {
    private final int zzaBk;
    private final WeakReference<Activity> zzaka;

    public gx(Context context, int i) {
        super();
        this.zzaka = new WeakReference((Activity) context);
        this.zzaBk = i;
    }

    public final void zza(int i, FullWallet fullWallet, Bundle bundle) {
        Activity activity = (Activity) this.zzaka.get();
        if (activity == null) {
            Log.d("WalletClientImpl", "Ignoring onFullWalletLoaded, Activity has gone");
            return;
        }
        PendingIntent pendingIntent = null;
        if (bundle != null) {
            pendingIntent = (PendingIntent) bundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT");
        }
        ConnectionResult connectionResult = new ConnectionResult(i, pendingIntent);
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, this.zzaBk);
                return;
            } catch (Throwable e) {
                Log.w("WalletClientImpl", "Exception starting pending intent", e);
                return;
            }
        }
        int i2;
        Intent intent = new Intent();
        if (connectionResult.isSuccess()) {
            i2 = -1;
            intent.putExtra(WalletConstants.EXTRA_FULL_WALLET, fullWallet);
        } else {
            i2 = i == 408 ? 0 : 1;
            intent.putExtra(WalletConstants.EXTRA_ERROR_CODE, i);
        }
        PendingIntent createPendingResult = activity.createPendingResult(this.zzaBk, intent, NUM);
        if (createPendingResult == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onFullWalletLoaded");
            return;
        }
        try {
            createPendingResult.send(i2);
        } catch (Throwable e2) {
            Log.w("WalletClientImpl", "Exception setting pending result", e2);
        }
    }

    public final void zza(int i, MaskedWallet maskedWallet, Bundle bundle) {
        Activity activity = (Activity) this.zzaka.get();
        if (activity == null) {
            Log.d("WalletClientImpl", "Ignoring onMaskedWalletLoaded, Activity has gone");
            return;
        }
        PendingIntent pendingIntent = null;
        if (bundle != null) {
            pendingIntent = (PendingIntent) bundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT");
        }
        ConnectionResult connectionResult = new ConnectionResult(i, pendingIntent);
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, this.zzaBk);
                return;
            } catch (Throwable e) {
                Log.w("WalletClientImpl", "Exception starting pending intent", e);
                return;
            }
        }
        int i2;
        Intent intent = new Intent();
        if (connectionResult.isSuccess()) {
            i2 = -1;
            intent.putExtra(WalletConstants.EXTRA_MASKED_WALLET, maskedWallet);
        } else {
            i2 = i == 408 ? 0 : 1;
            intent.putExtra(WalletConstants.EXTRA_ERROR_CODE, i);
        }
        PendingIntent createPendingResult = activity.createPendingResult(this.zzaBk, intent, NUM);
        if (createPendingResult == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onMaskedWalletLoaded");
            return;
        }
        try {
            createPendingResult.send(i2);
        } catch (Throwable e2) {
            Log.w("WalletClientImpl", "Exception setting pending result", e2);
        }
    }

    public final void zza(int i, boolean z, Bundle bundle) {
        Activity activity = (Activity) this.zzaka.get();
        if (activity == null) {
            Log.d("WalletClientImpl", "Ignoring onPreAuthorizationDetermined, Activity has gone");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(WalletConstants.EXTRA_IS_USER_PREAUTHORIZED, z);
        PendingIntent createPendingResult = activity.createPendingResult(this.zzaBk, intent, NUM);
        if (createPendingResult == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onPreAuthorizationDetermined");
            return;
        }
        try {
            createPendingResult.send(-1);
        } catch (Throwable e) {
            Log.w("WalletClientImpl", "Exception setting pending result", e);
        }
    }

    public final void zza(Status status, boolean z, Bundle bundle) {
        Activity activity = (Activity) this.zzaka.get();
        if (activity == null) {
            Log.d("WalletClientImpl", "Ignoring onIsReadyToPayDetermined, Activity has gone");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(WalletConstants.EXTRA_IS_READY_TO_PAY, z);
        PendingIntent createPendingResult = activity.createPendingResult(this.zzaBk, intent, NUM);
        if (createPendingResult == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onIsReadyToPayDetermined");
            return;
        }
        try {
            createPendingResult.send(-1);
        } catch (Throwable e) {
            Log.w("WalletClientImpl", "Exception setting pending result in onIsReadyToPayDetermined", e);
        }
    }

    public final void zzb(int i, boolean z, Bundle bundle) {
        Activity activity = (Activity) this.zzaka.get();
        if (activity == null) {
            Log.d("WalletClientImpl", "Ignoring onIsNewUserDetermined, Activity has gone");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(WalletConstants.EXTRA_IS_NEW_USER, z);
        PendingIntent createPendingResult = activity.createPendingResult(this.zzaBk, intent, NUM);
        if (createPendingResult == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onIsNewUserDetermined");
            return;
        }
        try {
            createPendingResult.send(-1);
        } catch (Throwable e) {
            Log.w("WalletClientImpl", "Exception setting pending result", e);
        }
    }

    public final void zzg(int i, Bundle bundle) {
        zzbo.zzb((Object) bundle, (Object) "Bundle should not be null");
        Activity activity = (Activity) this.zzaka.get();
        if (activity == null) {
            Log.d("WalletClientImpl", "Ignoring onWalletObjectsCreated, Activity has gone");
            return;
        }
        ConnectionResult connectionResult = new ConnectionResult(i, (PendingIntent) bundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT"));
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, this.zzaBk);
                return;
            } catch (Throwable e) {
                Log.w("WalletClientImpl", "Exception starting pending intent", e);
                return;
            }
        }
        String valueOf = String.valueOf(connectionResult);
        Log.e("WalletClientImpl", new StringBuilder(String.valueOf(valueOf).length() + 75).append("Create Wallet Objects confirmation UI will not be shown connection result: ").append(valueOf).toString());
        Intent intent = new Intent();
        intent.putExtra(WalletConstants.EXTRA_ERROR_CODE, WalletConstants.ERROR_CODE_UNKNOWN);
        PendingIntent createPendingResult = activity.createPendingResult(this.zzaBk, intent, NUM);
        if (createPendingResult == null) {
            Log.w("WalletClientImpl", "Null pending result returned for onWalletObjectsCreated");
            return;
        }
        try {
            createPendingResult.send(1);
        } catch (Throwable e2) {
            Log.w("WalletClientImpl", "Exception setting pending result", e2);
        }
    }
}
