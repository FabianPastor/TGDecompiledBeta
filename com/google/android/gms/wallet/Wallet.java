package com.google.android.gms.wallet;

import android.content.Context;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.VisibleForTesting;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.internal.zzbkx;
import com.google.android.gms.internal.zzbky;
import com.google.android.gms.internal.zzbla;
import com.google.android.gms.internal.zzblb;
import com.google.android.gms.wallet.firstparty.zzc;
import com.google.android.gms.wallet.wobs.zzr;
import java.util.Locale;

public final class Wallet {
    public static final Api<WalletOptions> API = new Api("Wallet.API", zzaie, zzaid);
    public static final Payments Payments = new zzbkx();
    private static final zzf<zzbky> zzaid = new zzf();
    private static final com.google.android.gms.common.api.Api.zza<zzbky, WalletOptions> zzaie = new com.google.android.gms.common.api.Api.zza<zzbky, WalletOptions>() {
        public zzbky zza(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, WalletOptions walletOptions, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            if (walletOptions == null) {
                walletOptions = new WalletOptions();
            }
            return new zzbky(context, looper, com_google_android_gms_common_internal_zzg, connectionCallbacks, onConnectionFailedListener, walletOptions.environment, walletOptions.theme, walletOptions.zzbRw);
        }
    };
    public static final zzr zzbRu = new zzblb();
    public static final zzc zzbRv = new zzbla();

    public static final class WalletOptions implements HasOptions {
        public final int environment;
        public final int theme;
        @VisibleForTesting
        final boolean zzbRw;

        public static final class Builder {
            private int mTheme = 0;
            private int zzbRx = 3;
            private boolean zzbRy = true;

            public WalletOptions build() {
                return new WalletOptions();
            }

            public Builder setEnvironment(int i) {
                if (i == 0 || i == 0 || i == 2 || i == 1 || i == 3) {
                    this.zzbRx = i;
                    return this;
                }
                throw new IllegalArgumentException(String.format(Locale.US, "Invalid environment value %d", new Object[]{Integer.valueOf(i)}));
            }

            public Builder setTheme(int i) {
                if (i == 0 || i == 1) {
                    this.mTheme = i;
                    return this;
                }
                throw new IllegalArgumentException(String.format(Locale.US, "Invalid theme value %d", new Object[]{Integer.valueOf(i)}));
            }

            @Deprecated
            public Builder useGoogleWallet() {
                this.zzbRy = false;
                return this;
            }
        }

        private WalletOptions() {
            this(new Builder());
        }

        private WalletOptions(Builder builder) {
            this.environment = builder.zzbRx;
            this.theme = builder.mTheme;
            this.zzbRw = builder.zzbRy;
        }
    }

    public static abstract class zza<R extends Result> extends com.google.android.gms.internal.zzaad.zza<R, zzbky> {
        public zza(GoogleApiClient googleApiClient) {
            super(Wallet.API, googleApiClient);
        }

        public /* synthetic */ void setResult(Object obj) {
            super.zzb((Result) obj);
        }

        @VisibleForTesting
        protected abstract void zza(zzbky com_google_android_gms_internal_zzbky) throws RemoteException;
    }

    public static abstract class zzb extends zza<Status> {
        public zzb(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        protected Status zzb(Status status) {
            return status;
        }

        protected /* synthetic */ Result zzc(Status status) {
            return zzb(status);
        }
    }

    private Wallet() {
    }

    @Deprecated
    public static void changeMaskedWallet(GoogleApiClient googleApiClient, String str, String str2, int i) {
        Payments.changeMaskedWallet(googleApiClient, str, str2, i);
    }

    @Deprecated
    public static void checkForPreAuthorization(GoogleApiClient googleApiClient, int i) {
        Payments.checkForPreAuthorization(googleApiClient, i);
    }

    @Deprecated
    public static void loadFullWallet(GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i) {
        Payments.loadFullWallet(googleApiClient, fullWalletRequest, i);
    }

    @Deprecated
    public static void loadMaskedWallet(GoogleApiClient googleApiClient, MaskedWalletRequest maskedWalletRequest, int i) {
        Payments.loadMaskedWallet(googleApiClient, maskedWalletRequest, i);
    }

    @Deprecated
    public static void notifyTransactionStatus(GoogleApiClient googleApiClient, NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        Payments.notifyTransactionStatus(googleApiClient, notifyTransactionStatusRequest);
    }
}
