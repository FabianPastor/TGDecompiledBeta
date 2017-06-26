package com.google.android.gms.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;

public final class gt extends zzz<ge> {
    private final Context mContext;
    private final int mTheme;
    private final String zzakh;
    private final int zzbPR;
    private final boolean zzbPS;

    public gt(Context context, Looper looper, zzq com_google_android_gms_common_internal_zzq, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, int i, int i2, boolean z) {
        super(context, looper, 4, com_google_android_gms_common_internal_zzq, connectionCallbacks, onConnectionFailedListener);
        this.mContext = context;
        this.zzbPR = i;
        this.zzakh = com_google_android_gms_common_internal_zzq.getAccountName();
        this.mTheme = i2;
        this.zzbPS = z;
    }

    private final Bundle zzDS() {
        int i = this.zzbPR;
        String packageName = this.mContext.getPackageName();
        Object obj = this.zzakh;
        int i2 = this.mTheme;
        boolean z = this.zzbPS;
        Bundle bundle = new Bundle();
        bundle.putInt("com.google.android.gms.wallet.EXTRA_ENVIRONMENT", i);
        bundle.putBoolean("com.google.android.gms.wallet.EXTRA_USING_ANDROID_PAY_BRAND", z);
        bundle.putString("androidPackageName", packageName);
        if (!TextUtils.isEmpty(obj)) {
            bundle.putParcelable("com.google.android.gms.wallet.EXTRA_BUYER_ACCOUNT", new Account(obj, "com.google"));
        }
        bundle.putInt("com.google.android.gms.wallet.EXTRA_THEME", i2);
        return bundle;
    }

    public final void zza(FullWalletRequest fullWalletRequest, int i) {
        gi gxVar = new gx(this.mContext, i);
        try {
            ((ge) zzrf()).zza(fullWalletRequest, zzDS(), gxVar);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException getting full wallet", e);
            gxVar.zza(8, null, Bundle.EMPTY);
        }
    }

    public final void zza(IsReadyToPayRequest isReadyToPayRequest, zzbay<BooleanResult> com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_BooleanResult) {
        gi gwVar = new gw(com_google_android_gms_internal_zzbay_com_google_android_gms_common_api_BooleanResult);
        try {
            ((ge) zzrf()).zza(isReadyToPayRequest, zzDS(), gwVar);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException during isReadyToPay", e);
            gwVar.zza(Status.zzaBo, false, Bundle.EMPTY);
        }
    }

    public final void zza(MaskedWalletRequest maskedWalletRequest, int i) {
        Bundle zzDS = zzDS();
        gi gxVar = new gx(this.mContext, i);
        try {
            ((ge) zzrf()).zza(maskedWalletRequest, zzDS, gxVar);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException getting masked wallet", e);
            gxVar.zza(8, null, Bundle.EMPTY);
        }
    }

    public final void zza(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        try {
            ((ge) zzrf()).zza(notifyTransactionStatusRequest, zzDS());
        } catch (RemoteException e) {
        }
    }

    public final void zzbP(int i) {
        Bundle zzDS = zzDS();
        gi gxVar = new gx(this.mContext, i);
        try {
            ((ge) zzrf()).zza(zzDS, gxVar);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException during checkForPreAuthorization", e);
            gxVar.zza(8, false, Bundle.EMPTY);
        }
    }

    public final void zzbQ(int i) {
        Bundle zzDS = zzDS();
        Object gxVar = new gx(this.mContext, i);
        try {
            ((ge) zzrf()).zzb(zzDS, gxVar);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException during isNewUser", e);
            gxVar.zzb(8, false, Bundle.EMPTY);
        }
    }

    public final void zzc(String str, String str2, int i) {
        Bundle zzDS = zzDS();
        Object gxVar = new gx(this.mContext, i);
        try {
            ((ge) zzrf()).zza(str, str2, zzDS, gxVar);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException changing masked wallet", e);
            gxVar.zza(8, null, Bundle.EMPTY);
        }
    }

    protected final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wallet.internal.IOwService");
        return queryLocalInterface instanceof ge ? (ge) queryLocalInterface : new gf(iBinder);
    }

    protected final String zzdb() {
        return "com.google.android.gms.wallet.service.BIND";
    }

    protected final String zzdc() {
        return "com.google.android.gms.wallet.internal.IOwService";
    }

    public final boolean zzrg() {
        return true;
    }
}
