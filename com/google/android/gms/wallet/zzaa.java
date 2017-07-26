package com.google.android.gms.wallet;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.internal.gu;
import com.google.android.gms.wallet.Wallet.WalletOptions;

final class zzaa extends zza<gu, WalletOptions> {
    zzaa() {
    }

    public final /* synthetic */ zze zza(Context context, Looper looper, zzq com_google_android_gms_common_internal_zzq, Object obj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        WalletOptions walletOptions = (WalletOptions) obj;
        if (walletOptions == null) {
            walletOptions = new WalletOptions();
        }
        return new gu(context, looper, com_google_android_gms_common_internal_zzq, connectionCallbacks, onConnectionFailedListener, walletOptions.environment, walletOptions.theme, walletOptions.zzbPS);
    }
}
