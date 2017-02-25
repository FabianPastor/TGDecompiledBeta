package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.Payments;
import com.google.android.gms.wallet.Wallet.zza;
import com.google.android.gms.wallet.Wallet.zzb;

@SuppressLint({"MissingRemoteException"})
public class zzbkx implements Payments {
    public void changeMaskedWallet(GoogleApiClient googleApiClient, String str, String str2, int i) {
        final String str3 = str;
        final String str4 = str2;
        final int i2 = i;
        googleApiClient.zza(new zzb(this, googleApiClient) {
            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zzf(str3, str4, i2);
                zzb(Status.zzazx);
            }
        });
    }

    public void checkForPreAuthorization(GoogleApiClient googleApiClient, final int i) {
        googleApiClient.zza(new zzb(this, googleApiClient) {
            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zzoY(i);
                zzb(Status.zzazx);
            }
        });
    }

    public void isNewUser(GoogleApiClient googleApiClient, final int i) {
        googleApiClient.zza(new zzb(this, googleApiClient) {
            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zzoZ(i);
                zzb(Status.zzazx);
            }
        });
    }

    public PendingResult<BooleanResult> isReadyToPay(GoogleApiClient googleApiClient) {
        return googleApiClient.zza(new zza<BooleanResult>(this, googleApiClient) {
            protected BooleanResult zzM(Status status) {
                return new BooleanResult(status, false);
            }

            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zza(IsReadyToPayRequest.newBuilder().build(), (zzaad.zzb) this);
            }

            protected /* synthetic */ Result zzc(Status status) {
                return zzM(status);
            }
        });
    }

    public PendingResult<BooleanResult> isReadyToPay(GoogleApiClient googleApiClient, final IsReadyToPayRequest isReadyToPayRequest) {
        return googleApiClient.zza(new zza<BooleanResult>(this, googleApiClient) {
            protected BooleanResult zzM(Status status) {
                return new BooleanResult(status, false);
            }

            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zza(isReadyToPayRequest, (zzaad.zzb) this);
            }

            protected /* synthetic */ Result zzc(Status status) {
                return zzM(status);
            }
        });
    }

    public void loadFullWallet(GoogleApiClient googleApiClient, final FullWalletRequest fullWalletRequest, final int i) {
        googleApiClient.zza(new zzb(this, googleApiClient) {
            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zza(fullWalletRequest, i);
                zzb(Status.zzazx);
            }
        });
    }

    public void loadMaskedWallet(GoogleApiClient googleApiClient, final MaskedWalletRequest maskedWalletRequest, final int i) {
        googleApiClient.zza(new zzb(this, googleApiClient) {
            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zza(maskedWalletRequest, i);
                zzb(Status.zzazx);
            }
        });
    }

    public void notifyTransactionStatus(GoogleApiClient googleApiClient, final NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        googleApiClient.zza(new zzb(this, googleApiClient) {
            protected void zza(zzbky com_google_android_gms_internal_zzbky) {
                com_google_android_gms_internal_zzbky.zza(notifyTransactionStatusRequest);
                zzb(Status.zzazx);
            }
        });
    }
}
