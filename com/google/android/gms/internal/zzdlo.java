package com.google.android.gms.internal;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzn;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public final class zzdlo extends zzab<zzdlb> {
    private final Context mContext;
    private final int mTheme;
    private final String zzebv;
    private final int zzlea;
    private final boolean zzleb;

    public zzdlo(Context context, Looper looper, zzr com_google_android_gms_common_internal_zzr, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener, int i, int i2, boolean z) {
        super(context, looper, 4, com_google_android_gms_common_internal_zzr, connectionCallbacks, onConnectionFailedListener);
        this.mContext = context;
        this.zzlea = i;
        this.zzebv = com_google_android_gms_common_internal_zzr.getAccountName();
        this.mTheme = i2;
        this.zzleb = z;
    }

    private final Bundle zzbka() {
        int i = this.zzlea;
        String packageName = this.mContext.getPackageName();
        Object obj = this.zzebv;
        int i2 = this.mTheme;
        boolean z = this.zzleb;
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
        zzdlf com_google_android_gms_internal_zzdlp = new zzdlp((Activity) this.mContext, i);
        try {
            ((zzdlb) zzakn()).zza(fullWalletRequest, zzbka(), com_google_android_gms_internal_zzdlp);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException getting full wallet", e);
            com_google_android_gms_internal_zzdlp.zza(8, null, Bundle.EMPTY);
        }
    }

    public final void zza(IsReadyToPayRequest isReadyToPayRequest, zzn<BooleanResult> com_google_android_gms_common_api_internal_zzn_com_google_android_gms_common_api_BooleanResult) {
        zzdlf com_google_android_gms_internal_zzdlt = new zzdlt(com_google_android_gms_common_api_internal_zzn_com_google_android_gms_common_api_BooleanResult);
        try {
            ((zzdlb) zzakn()).zza(isReadyToPayRequest, zzbka(), com_google_android_gms_internal_zzdlt);
        } catch (Throwable e) {
            Log.e("WalletClientImpl", "RemoteException during isReadyToPay", e);
            com_google_android_gms_internal_zzdlt.zza(Status.zzfnk, false, Bundle.EMPTY);
        }
    }

    public final boolean zzako() {
        return true;
    }

    protected final /* synthetic */ IInterface zzd(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.wallet.internal.IOwService");
        return queryLocalInterface instanceof zzdlb ? (zzdlb) queryLocalInterface : new zzdlc(iBinder);
    }

    protected final String zzhi() {
        return "com.google.android.gms.wallet.service.BIND";
    }

    protected final String zzhj() {
        return "com.google.android.gms.wallet.internal.IOwService";
    }
}
