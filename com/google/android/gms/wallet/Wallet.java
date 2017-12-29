package com.google.android.gms.wallet;

import android.accounts.Account;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.HasAccountOptions;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzm;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.internal.zzdkp;
import com.google.android.gms.internal.zzdlh;
import com.google.android.gms.internal.zzdlo;
import com.google.android.gms.internal.zzdlw;
import com.google.android.gms.internal.zzdlx;
import com.google.android.gms.wallet.wobs.WalletObjects;
import java.util.Arrays;
import java.util.Locale;

public final class Wallet {
    public static final Api<WalletOptions> API = new Api("Wallet.API", zzebg, zzebf);
    @Deprecated
    public static final Payments Payments = new zzdlh();
    private static final zzf<zzdlo> zzebf = new zzf();
    private static final com.google.android.gms.common.api.Api.zza<zzdlo, WalletOptions> zzebg = new zzap();
    private static WalletObjects zzldx = new zzdlx();
    private static zzdkp zzldy = new zzdlw();

    public static final class WalletOptions implements HasAccountOptions {
        private Account account;
        public final int environment;
        public final int theme;
        final boolean zzldz;

        public static final class Builder {
            private int mTheme = 0;
            private int zzlea = 3;
            private boolean zzleb = true;

            public final WalletOptions build() {
                return new WalletOptions();
            }

            public final Builder setEnvironment(int i) {
                if (i == 0 || i == 0 || i == 2 || i == 1 || i == 3) {
                    this.zzlea = i;
                    return this;
                }
                throw new IllegalArgumentException(String.format(Locale.US, "Invalid environment value %d", new Object[]{Integer.valueOf(i)}));
            }

            public final Builder setTheme(int i) {
                if (i == 0 || i == 1) {
                    this.mTheme = i;
                    return this;
                }
                throw new IllegalArgumentException(String.format(Locale.US, "Invalid theme value %d", new Object[]{Integer.valueOf(i)}));
            }
        }

        private WalletOptions() {
            this(new Builder());
        }

        private WalletOptions(Builder builder) {
            this.environment = builder.zzlea;
            this.theme = builder.mTheme;
            this.zzldz = builder.zzleb;
            this.account = null;
        }

        public final boolean equals(Object obj) {
            if (!(obj instanceof WalletOptions)) {
                return false;
            }
            WalletOptions walletOptions = (WalletOptions) obj;
            return zzbg.equal(Integer.valueOf(this.environment), Integer.valueOf(walletOptions.environment)) && zzbg.equal(Integer.valueOf(this.theme), Integer.valueOf(walletOptions.theme)) && zzbg.equal(null, null) && zzbg.equal(Boolean.valueOf(this.zzldz), Boolean.valueOf(walletOptions.zzldz));
        }

        public final Account getAccount() {
            return null;
        }

        public final int hashCode() {
            return Arrays.hashCode(new Object[]{Integer.valueOf(this.environment), Integer.valueOf(this.theme), null, Boolean.valueOf(this.zzldz)});
        }
    }

    public static abstract class zza<R extends Result> extends zzm<R, zzdlo> {
        public zza(GoogleApiClient googleApiClient) {
            super(Wallet.API, googleApiClient);
        }

        public final /* bridge */ /* synthetic */ void setResult(Object obj) {
            super.setResult((Result) obj);
        }

        protected abstract void zza(zzdlo com_google_android_gms_internal_zzdlo) throws RemoteException;
    }

    public static abstract class zzb extends zza<Status> {
        public zzb(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        protected final /* synthetic */ Result zzb(Status status) {
            return status;
        }
    }
}
